package com.brt.passenger.kafka;

import com.brt.passenger.domain.model.*;
import com.brt.passenger.domain.events.PaymentFailedEvent;
import com.brt.passenger.domain.events.SubscriptionCreatedEvent;
import com.brt.passenger.domain.events.TicketPurchasedEvent;
import com.brt.passenger.domain.events.TicketValidatedEvent;
import com.brt.passenger.repository.PassengerRepository;
import com.brt.passenger.repository.TripHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketingEventConsumer {

    private final PassengerRepository passengerRepository;
    private final TripHistoryRepository tripHistoryRepository;
    private final ObjectMapper objectMapper;

    // ── ticket.purchased → mettre à jour l'historique ──────────

    @KafkaListener(
            topics = "ticket.purchased",
            groupId = "passenger-ticketing-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void onTicketPurchased(String message) {
        try {
            TicketPurchasedEvent event = objectMapper
                    .readValue(message, TicketPurchasedEvent.class);

            log.info("TicketPurchased reçu : {}", event.getTicketId());

            passengerRepository.findById(UUID.fromString(event.getPassengerId()))
                    .ifPresent(passenger -> {
                        log.info("Ticket acheté pour passager : {}",
                                event.getPassengerId());
                    });

        } catch (Exception e) {
            log.error("Erreur onTicketPurchased : {}", e.getMessage());
        }
    }

    // ── ticket.validated → enregistrer le trajet ───────────────

    @KafkaListener(
            topics = "ticket.validated",
            groupId = "passenger-ticketing-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void onTicketValidated(String message) {
        try {
            TicketValidatedEvent event = objectMapper
                    .readValue(message, TicketValidatedEvent.class);

            log.info("TicketValidated reçu : {}", event.getTicketId());

            passengerRepository.findById(UUID.fromString(event.getPassengerId()))
                    .ifPresent(passager -> {
                        TripHistory trip = TripHistory.builder()
                                .passager(passager)
                                .ticketId(UUID.fromString(event.getTicketId()))
                                .lineId(event.getLineId())
                                .boardingStation(event.getStationId())
                                .amountPaid(event.getAmount())
                                .tripDate(event.getTimestamp())
                                .build();
                        tripHistoryRepository.save(trip);
                        log.info("Trajet enregistré pour : {}",
                                event.getPassengerId());
                    });

        } catch (Exception e) {
            log.error("Erreur onTicketValidated : {}", e.getMessage());
        }
    }

    // ── payment.failed → noter l'échec ─────────────────────────

    @KafkaListener(
            topics = "payment.failed",
            groupId = "passenger-ticketing-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void onPaymentFailed(String message) {
        try {
            PaymentFailedEvent event = objectMapper
                    .readValue(message, PaymentFailedEvent.class);

            log.warn("PaymentFailed reçu pour passager : {}",
                    event.getPassengerId());


        } catch (Exception e) {
            log.error("Erreur onPaymentFailed : {}", e.getMessage());
        }
    }

    // ── subscription.created → mettre à jour l'abonnement ──────

    @KafkaListener(
            topics = "subscription.created",
            groupId = "passenger-ticketing-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    @Transactional
    public void onSubscriptionCreated(String message) {
        try {
            SubscriptionCreatedEvent event = objectMapper
                    .readValue(message, SubscriptionCreatedEvent.class);

            log.info("SubscriptionCreated reçu : {}", event.getAbonnementId());

            passengerRepository.findById(UUID.fromString(event.getPassengerId()))
                    .ifPresent(passenger -> {

                        Abonnement abonnement = Abonnement.builder()
                                .passager(passenger)
                                .type(SubscriptionType.valueOf(event.getType()))
                                .dateDebut(LocalDate.now())
                                .dateFin(LocalDate.parse(event.getDateFin()))
                                .tarif(event.getPrice())
                                .actif(true)
                                .build();

                        passenger.getAbonnements().add(abonnement);

                        passengerRepository.save(passenger);

                        log.info("Subscription ajoutée pour passenger : {}",
                                event.getPassengerId());
                    });

        } catch (Exception e) {
            log.error("Erreur onSubscriptionCreated : {}", e.getMessage());
        }
    }
}
