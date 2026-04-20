package com.brt.fleetmanagementservice.service;

import com.brt.fleetmanagementservice.entity.Affectation;
import com.brt.fleetmanagementservice.event.BusAssignedToLineEvent;
import com.brt.fleetmanagementservice.repository.AffectationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AffectationService {

    private final AffectationRepository affectationRepo;
    private final BusService            busService;
    private final ConducteurService     conducteurService;
    private final ApplicationEventPublisher eventPublisher;

    public Affectation creerAffectation(Affectation affectation) {

        UUID busId        = affectation.getBus().getId();
        UUID conducteurId = affectation.getConducteur().getId();
        LocalDateTime debut = affectation.getDateDebut();
        LocalDateTime fin   = affectation.getDateFin();

        if (!fin.isAfter(debut)) {
            throw new RuntimeException(
                    "La date de fin doit être postérieure à la date de début.");
        }

        if (!busService.estDisponible(busId)) {
            throw new RuntimeException(
                    "Le bus " + busId + " n'est pas disponible.");
        }

        if (affectationRepo.existsConflitBus(busId, debut, fin)) {
            throw new RuntimeException(
                    "Le bus est déjà affecté sur ce créneau horaire.");
        }

        if (!conducteurService.estDisponible(conducteurId, debut, fin)) {
            throw new RuntimeException(
                    "Le conducteur " + conducteurId
                            + " n'est pas disponible sur ce créneau.");
        }

        affectation.setStatut("ACTIVE");
        Affectation saved = affectationRepo.save(affectation);

        busService.changerStatut(busId, "AFFECTE");
        conducteurService.changerStatut(conducteurId, "EN_SERVICE");

        // ── Émission de l'événement BusAssignedToLine ─────────
        eventPublisher.publishEvent(new BusAssignedToLineEvent(
                this,
                saved.getId(),
                busId,
                conducteurId,
                affectation.getLigneId(),
                debut,
                fin
        ));

        return saved;
    }
    // Ajouter dans AffectationService

    public List<Affectation> getAffectationsByLigne(UUID ligneId) {
        return affectationRepo.findByLigneId(ligneId);
    }

    public Affectation annulerAffectation(UUID id) {
        Affectation affectation = getAffectationById(id);

        if (affectation.getStatut().equalsIgnoreCase("TERMINEE")) {
            throw new RuntimeException(
                    "Impossible d'annuler une affectation déjà terminée.");
        }

        if (affectation.getStatut().equalsIgnoreCase("ANNULEE")) {
            throw new RuntimeException("L'affectation est déjà annulée.");
        }

        affectation.setStatut("ANNULEE");
        affectationRepo.save(affectation);

        busService.changerStatut(
                affectation.getBus().getId(), "DISPONIBLE");
        conducteurService.changerStatut(
                affectation.getConducteur().getId(), "DISPONIBLE");

        return affectation;
    }


    public Affectation getAffectationById(UUID id) {
        return affectationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation introuvable : " + id));
    }

    public List<Affectation> getToutesAffectations() {
        return affectationRepo.findAll();
    }
}