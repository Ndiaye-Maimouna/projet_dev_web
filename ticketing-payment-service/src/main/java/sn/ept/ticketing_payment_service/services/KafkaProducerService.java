package sn.ept.ticketing_payment_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sn.ept.ticketing_payment_service.events.PaymentFailedEvent;
import sn.ept.ticketing_payment_service.events.SubscriptionCreatedEvent;
import sn.ept.ticketing_payment_service.events.TicketPurchasedEvent;
import sn.ept.ticketing_payment_service.events.TicketValidatedEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${brt.kafka.topics.ticket-purchased}")
    private String ticketPurchasedTopic;

    @Value("${brt.kafka.topics.ticket-validated}")
    private String ticketValidatedTopic;

    @Value("${brt.kafka.topics.payment-failed}")
    private String paymentFailedTopic;

    @Value("${brt.kafka.topics.subscription-created}")
    private String subscriptionCreatedTopic;

    public void publishTicketPurchased(TicketPurchasedEvent event) {
        kafkaTemplate.send(ticketPurchasedTopic, event.getTicketId(), event);
        log.info("TicketPurchased publié : {}", event.getTicketId());
    }

    public void publishTicketValidated(TicketValidatedEvent event) {
        kafkaTemplate.send(ticketValidatedTopic, event.getTicketId(), event);
        log.info("TicketValidated publié : {}", event.getTicketId());
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        kafkaTemplate.send(paymentFailedTopic, event.getPaymentId(), event);
        log.warn("PaymentFailed publié : {}", event.getPaymentId());
    }

    public void publishSubscriptionCreated(SubscriptionCreatedEvent event) {
        kafkaTemplate.send(subscriptionCreatedTopic, event.getAbonnementId(), event);
        log.info("SubscriptionCreated publié : {}", event.getAbonnementId());
    }
}
