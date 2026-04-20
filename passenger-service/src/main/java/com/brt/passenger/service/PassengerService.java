package com.brt.passenger.service;

import com.brt.passenger.domain.events.PassengerEvents;
import com.brt.passenger.domain.model.Passager;
import com.brt.passenger.domain.model.PassagerStatus;
import com.brt.passenger.domain.model.TripHistory;
import com.brt.passenger.dto.request.CreatePassagerRequest;
import com.brt.passenger.dto.request.UpdatePassengerRequest;
import com.brt.passenger.dto.response.AbonnementResponse;
import com.brt.passenger.dto.response.PassengerResponse;
import com.brt.passenger.dto.response.TripHistoryResponse;
import com.brt.passenger.exception.DuplicatePassengerException;
import com.brt.passenger.exception.PassengerNotFoundException;
import com.brt.passenger.kafka.PassengerEventProducer;
import com.brt.passenger.repository.AbonnementRepository;
import com.brt.passenger.repository.PassengerRepository;
import com.brt.passenger.repository.TripHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Service métier du passenger-service.
 *
 * ↑ Fournit :
 *   creerPassager, getPassagerById, updatePassager,
 *   supprimerPassager, getHistorique, getAbonnements
 *
 * ↓ Consomme (via Kafka) :
 *   ticket.validated → enregistre dans l'historique
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassengerService {

    private final PassengerRepository    passengerRepository;
    private final TripHistoryRepository  tripHistoryRepository;
    private final AbonnementRepository   abonnementRepository;
    private final PassengerEventProducer eventProducer;

    // ── POST /passengers — créer un compte ────────────────────────────

    @Transactional
    public PassengerResponse creerPassager(CreatePassagerRequest request) {
        if (passengerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicatePassengerException("email", request.getEmail());
        }
        if (passengerRepository.existsByTelephone(request.getTelephone())) {
            throw new DuplicatePassengerException("téléphone", request.getTelephone());
        }

        Passager passager = Passager.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail().toLowerCase().trim())
                .telephone(request.getTelephone())
                .statut(PassagerStatus.ACTIVE)
                .build();

        Passager saved = passengerRepository.save(passager);
        log.info("Passager créé : {} {} (id={})", saved.getPrenom(), saved.getNom(), saved.getId());

        eventProducer.publishPassengerRegistered(
                PassengerEvents.PassengerRegistered.builder()
                        .passengerId(saved.getId())
                        .firstName(saved.getPrenom())
                        .lastName(saved.getNom())
                        .email(saved.getEmail())
                        .phoneNumber(saved.getTelephone())
                        .registeredAt(Instant.now())
                        .build()
        );

        return toResponse(saved);
    }

    // ── GET /passengers/{id} — profil ────────────────────────────────

    @Cacheable(value = "passengers", key = "#id")
    public PassengerResponse getPassagerById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    // ── GET /passengers/{/by-user/{userId}} — profil par user-id ────────────────────────────────

    public PassengerResponse getByUserId(UUID userId) {
        return toResponse(findByUserIdOrThrow(userId));
    }
    private Passager findByUserIdOrThrow(UUID userId) {
        return passengerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Passager introuvable avec userId: " + userId));
    }

    // ── PUT /passengers/{id} — mettre à jour ─────────────────────────

    @Transactional
    @CacheEvict(value = "passengers", key = "#id")
    public PassengerResponse updatePassager(UUID id, UpdatePassengerRequest request) {
        Passager passager = findOrThrow(id);

        if (request.getFirstName()   != null) passager.setPrenom(request.getFirstName());
        if (request.getLastName()    != null) passager.setNom(request.getLastName());
        if (request.getEmail()       != null
                && !request.getEmail().equals(passager.getEmail())) {
            if (passengerRepository.existsByEmail(request.getEmail()))
                throw new DuplicatePassengerException("email", request.getEmail());
            passager.setEmail(request.getEmail().toLowerCase().trim());
        }
        if (request.getPhoneNumber() != null
                && !request.getPhoneNumber().equals(passager.getTelephone())) {
            if (passengerRepository.existsByTelephone(request.getPhoneNumber()))
                throw new DuplicatePassengerException("téléphone", request.getPhoneNumber());
            passager.setTelephone(request.getPhoneNumber());
        }

        Passager updated = passengerRepository.save(passager);
        eventProducer.publishPassengerUpdated(
                PassengerEvents.PassengerUpdated.builder()
                        .passengerId(updated.getId())
                        .email(updated.getEmail())
                        .phoneNumber(updated.getTelephone())
                        .updatedAt(Instant.now())
                        .build()
        );
        return toResponse(updated);
    }

    // ── DELETE /passengers/{id} — supprimer ──────────────────────────

    @Transactional
    @CacheEvict(value = "passengers", key = "#id")
    public void supprimerPassager(UUID id) {
        Passager passager = findOrThrow(id);
        passengerRepository.delete(passager);
        log.info("Passager supprimé : {}", id);

        eventProducer.publishPassengerDeactivated(
                PassengerEvents.PassengerDeactivated.builder()
                        .passengerId(id)
                        .reason("Suppression du compte")
                        .deactivatedAt(Instant.now())
                        .build()
        );
    }

    // ── GET /passengers/{id}/historique ──────────────────────────────
    /**
     * Spec : historique des trajets.
     * Les trajets sont enregistrés localement via l'événement Kafka
     * ticket.validated (consommé depuis ticketing-service).
     */
    public Page<TripHistoryResponse> getHistorique(UUID passagerId, Pageable pageable) {
        if (!passengerRepository.existsById(passagerId))
            throw new PassengerNotFoundException(passagerId);

        return tripHistoryRepository
                .findByPassagerIdOrderByTripDateDesc(passagerId, pageable)
                .map(this::toTripResponse);
    }

    // ── GET /passengers/{id}/abonnements ─────────────────────────────
    /**
     * Spec : abonnements actifs du passager.
     */
    public List<AbonnementResponse> getAbonnements(UUID passagerId) {
        if (!passengerRepository.existsById(passagerId))
            throw new PassengerNotFoundException(passagerId);

        return abonnementRepository.findByPassagerIdAndActifTrue(passagerId)
                .stream()
                .map(a -> AbonnementResponse.builder()
                        .id(a.getId())
                        .type(a.getType())
                        .dateDebut(a.getDateDebut())
                        .dateFin(a.getDateFin())
                        .tarif(a.getTarif())
                        .actif(a.isActif())
                        .build())
                .toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private Passager findOrThrow(UUID id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException(id));
    }

    private PassengerResponse toResponse(Passager p) {
        return PassengerResponse.builder()
                .id(p.getId())
                .passengerUserId(p.getUserId())
                .firstName(p.getPrenom())
                .lastName(p.getNom())
                .fullName(p.getNomComplet())
                .email(p.getEmail())
                .phoneNumber(p.getTelephone())
                .status(p.getStatut())
                .hasActiveSubscription(p.aUnAbonnementActif())
                .createdAt(p.getCreeLe())
                .updatedAt(p.getModifieLe())
                .build();
    }

    private TripHistoryResponse toTripResponse(TripHistory t) {
        return TripHistoryResponse.builder()
                .id(t.getId())
                .ticketId(t.getTicketId())
                .lineId(t.getLineId())
                .boardingStation(t.getBoardingStation())
                .alightingStation(t.getAlightingStation())
                .tripDate(t.getTripDate())
                .amountPaid(t.getAmountPaid())
                .build();
    }
}
