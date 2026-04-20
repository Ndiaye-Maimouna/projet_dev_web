package sn.ept.ticketing_payment_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.ept.ticketing_payment_service.clients.PassengerClient;
import sn.ept.ticketing_payment_service.dtos.AbonnementRequest;
import sn.ept.ticketing_payment_service.dtos.AbonnementResponse;
import sn.ept.ticketing_payment_service.dtos.PassengerResponse;
import sn.ept.ticketing_payment_service.entities.Abonnement;
import sn.ept.ticketing_payment_service.entities.Payment;
import sn.ept.ticketing_payment_service.enums.AbonnementType;
import sn.ept.ticketing_payment_service.enums.PaymentStatus;
import sn.ept.ticketing_payment_service.events.SubscriptionCreatedEvent;
import sn.ept.ticketing_payment_service.exceptions.BusinessException;
import sn.ept.ticketing_payment_service.exceptions.ResourceNotFoundException;
import sn.ept.ticketing_payment_service.repositories.AbonnementRepository;
import sn.ept.ticketing_payment_service.repositories.PaymentRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AbonnementService {

    private final AbonnementRepository abonnementRepository;
    private final KafkaProducerService kafkaProducerService;
    private final PaymentRepository paymentRepository;
    private final PassengerClient passengerClient;



    public AbonnementResponse createSubscription(
            AbonnementRequest request, String userId) {

        BigDecimal tarif = calculateTarif(
                AbonnementType.valueOf(request.getType()));

        PassengerResponse passenger = getPassengerByUserId(userId);


        Abonnement abonnement = Abonnement.builder()
                .passengerId(passenger.getId().toString())
                .type(AbonnementType.valueOf(request.getType()))
                .dateDebut(LocalDate.now())
                .dateFin(calculateDateFin(AbonnementType.valueOf(request.getType())))
                .tarif(tarif)
                .actif(true)
                .build();

        abonnementRepository.save(abonnement);

        // Créer le payment pour l'abonnement
        Payment payment = Payment.builder()
                .passengerId(passenger.getId().toString())
                .abonnementId(abonnement.getId())
                .montant(tarif)
                .methode(request.getPaymentMethod())
                .phoneNumber(passenger.getPhoneNumber())
                .statut(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        // Publier SubscriptionCreated via Kafka
        kafkaProducerService.publishSubscriptionCreated(
                SubscriptionCreatedEvent.builder()
                        .abonnementId(abonnement.getId().toString())
                        .passengerId(passenger.getId().toString())
                        .passengerUserId(userId)
                        .type(request.getType())
                        .dateFin(abonnement.getDateFin().toString())
                        .timestamp(Instant.now())
                        .price(abonnement.getTarif())
                        .build()
        );

        return toAbonnementResponse(abonnement);
    }

    public List<AbonnementResponse> getAllAbonnements() {
        return abonnementRepository.findAll()
                .stream().map(this::toAbonnementResponse).toList();
    }

    public List<AbonnementResponse> getMyAbonnements(String userId) {

        PassengerResponse passenger = getPassengerByUserId(userId);
        List<Abonnement> abonnements =
                abonnementRepository.findByPassengerIdAndActifTrue(passenger.getId().toString());
        if (abonnements.isEmpty()) {
            throw new BusinessException("Aucun abonnement actif");
        }
        return abonnements.stream()
                .map(this::toAbonnementResponse)
                .toList();
    }

    public void deactivateSubscription(UUID id, String userId) {
        PassengerResponse passenger = getPassengerByUserId(userId);
        Abonnement abonnement = abonnementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement introuvable : " + id));

        if (!abonnement.getPassengerId().equals(passenger.getId().toString())) {
            throw new BusinessException("Accès refusé");
        }

        abonnement.setActif(false);
        abonnementRepository.save(abonnement);
    }

    public AbonnementResponse getSubscription(UUID id, String userId) {
        PassengerResponse passenger = getPassengerByUserId(userId);
        Abonnement abonnement = abonnementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement introuvable" + id));

        if (!abonnement.getPassengerId().equals(passenger.getId().toString())) {
            throw new BusinessException("Accès refusé");
        }

        return toAbonnementResponse(abonnement);
    }

    private BigDecimal calculateTarif(AbonnementType type) {
        return switch (type) {
            case WEEKLY  -> new BigDecimal("5000");
            case MONTHLY -> new BigDecimal("15000");
            case ANNUAL  -> new BigDecimal("150000");
        };
    }

    private LocalDate calculateDateFin(AbonnementType type) {
        return switch (type) {
            case WEEKLY  -> LocalDate.now().plusWeeks(1);
            case MONTHLY -> LocalDate.now().plusMonths(1);
            case ANNUAL  -> LocalDate.now().plusYears(1);
        };
    }

    private AbonnementResponse toAbonnementResponse(Abonnement abonnement) {
        return AbonnementResponse.builder()
                .id(abonnement.getId())
                .passengerId(abonnement.getPassengerId())
                .type(abonnement.getType())
                .dateDebut(abonnement.getDateDebut())
                .dateFin(abonnement.getDateFin())
                .tarif(abonnement.getTarif())
                .actif(abonnement.isActif())
                .build();
    }

    private PassengerResponse getPassengerByUserId(String userId) {
        try {
            return passengerClient.getByUserId(UUID.fromString(userId));
        } catch (Exception e) {
            throw new RuntimeException("Passager introuvable pour userId: " + userId);
        }
    }
}
