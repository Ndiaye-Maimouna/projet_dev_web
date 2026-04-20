package com.brt.passenger.kafka;

import com.brt.passenger.domain.events.PassengerEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Producteur Kafka du passenger-service.
 *
 * Publie les événements de domaine sur les topics configurés.
 * Utilise l'API async de KafkaTemplate pour ne pas bloquer le thread.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PassengerEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${brt.kafka.topics.passenger-registered}")
    private String passengerRegisteredTopic;

    @Value("${brt.kafka.topics.passenger-updated}")
    private String passengerUpdatedTopic;

    @Value("${brt.kafka.topics.passenger-deactivated}")
    private String passengerDeactivatedTopic;

    @Value("${brt.kafka.topics.passenger-entered-station}")
    private String passengerEnteredStationTopic;

    // ── Publish methods ────────────────────────────────────────────────

    public void publishPassengerRegistered(PassengerEvents.PassengerRegistered event) {
        send(passengerRegisteredTopic, event.getPassengerId().toString(), event);
    }

    public void publishPassengerUpdated(PassengerEvents.PassengerUpdated event) {
        send(passengerUpdatedTopic, event.getPassengerId().toString(), event);
    }

    public void publishPassengerDeactivated(PassengerEvents.PassengerDeactivated event) {
        send(passengerDeactivatedTopic, event.getPassengerId().toString(), event);
    }

    public void publishPassengerEnteredStation(PassengerEvents.PassengerEnteredStation event) {
        send(passengerEnteredStationTopic, event.getPassengerId().toString(), event);
    }

    // ── Internal helper ────────────────────────────────────────────────

    private void send(String topic, String key, Object payload) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, payload);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Échec de publication sur le topic '{}' (key={}): {}",
                        topic, key, ex.getMessage(), ex);
            } else {
                log.debug("Événement publié sur '{}' | partition={} | offset={}",
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
