package com.brt.passenger.kafka;

import com.brt.passenger.domain.events.PassengerEvents;
import com.brt.passenger.domain.events.TicketValidatedEvent;
import com.brt.passenger.domain.model.Passager;
import com.brt.passenger.domain.model.TripHistory;
import com.brt.passenger.domain.events.UserRegisteredEvent;
import com.brt.passenger.repository.PassengerRepository;
import com.brt.passenger.repository.TripHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PassengerEventConsumer {

    private final PassengerRepository    passengerRepository;
    private final TripHistoryRepository  tripHistoryRepository;
    private final PassengerEventProducer eventProducer;

    @KafkaListener(
            topics    = "ticket.validated",
            groupId   = "passenger-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void onTicketValidated(
            @Payload TicketValidatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("TicketValidated reçu | passengerId={} | stationId={} | topic={} | partition={} | offset={}",
                event.getPassengerId(), event.getStationId(), topic, partition, offset);

        // On utilise Passager (la vraie entité du diagramme)
        Passager passager = passengerRepository.findById(UUID.fromString(event.getPassengerId()))
                .orElseGet(() -> {
                    log.warn("Passager introuvable pour ticketValidated : {}", event.getPassengerId());
                    return null;
                });

        if (passager == null) return;

        TripHistory trip = TripHistory.builder()
                .passager(passager)               // ← mappedBy="passager" dans TripHistory
                .ticketId(UUID.fromString(event.getTicketId()))
                .lineId(event.getLineId())
                .boardingStation(event.getStationId())
                .amountPaid(event.getAmount())
                .tripDate(event.getTimestamp() != null ? event.getTimestamp() : Instant.now())
                .build();

        tripHistoryRepository.save(trip);
        log.debug("Trajet enregistré pour passager {} | ligne {}", passager.getId(), event.getLineId());

        PassengerEvents.PassengerEnteredStation enteredStation = PassengerEvents.PassengerEnteredStation.builder()
                .passengerId(passager.getId())
                .stationId(event.getStationId())
                .timestamp(Instant.now())
                .build();

        eventProducer.publishPassengerEnteredStation(enteredStation);
    }

    @KafkaListener(
            topics = "user.registered",
            groupId = "passenger-user-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void onUserRegistered(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserRegisteredEvent event = objectMapper.readValue(message, UserRegisteredEvent.class);

            if (!event.getRole().equals("PASSENGER")) return;

            Passager passager = Passager.builder()
                    .email(event.getEmail())
                    .nom(event.getLastName())        // lastName → nom
                    .prenom(event.getFirstName())    // firstName → prenom
                    .telephone(event.getPhone())     // phone → telephone
                    .userId(UUID.fromString(event.getUserId()))
                    .build();

            passengerRepository.save(passager);
            log.info("Passager créé : {}", event.getEmail());

        } catch (Exception e) {
            log.error("Erreur désérialisation UserRegisteredEvent : {}", e.getMessage());
        }
    }
}