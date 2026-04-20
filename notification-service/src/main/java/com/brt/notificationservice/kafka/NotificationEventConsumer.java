package com.brt.notificationservice.kafka;

import com.brt.notificationservice.entities.Notification;
import com.brt.notificationservice.enums.NotificationCanal;
import com.brt.notificationservice.enums.NotificationType;
import com.brt.notificationservice.enums.UserType;
import com.brt.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    // ── ticket.purchased ───────────────────────────────────────

    @KafkaListener(
            topics = "ticket.purchased",
            groupId = "notification-service-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void onTicketPurchased(String message) {
        try {
            TicketPurchasedEvent event = objectMapper
                    .readValue(message, TicketPurchasedEvent.class);

            notificationService.create(Notification.builder()
                    .userId(event.getPassengerUserId())
                    .userType(UserType.PASSENGER)
                    .canal(NotificationCanal.SMS)
                    .type(NotificationType.TICKET_PURCHASED)
                    .sujet("Ticket acheté")
                    .contenu("Votre ticket pour la ligne "
                            + event.getLineId()
                            + " est prêt. Montant : "
                            + event.getAmount() + " FCFA.")
                    .evenementSource("ticket.purchased")
                    .referenceId(event.getTicketId())
                    .build());

        } catch (Exception e) {
            log.error("Erreur onTicketPurchased : {}", e.getMessage());
        }
    }

    // ── ticket.validated ───────────────────────────────────────

    @KafkaListener(
            topics = "ticket.validated",
            groupId = "notification-service-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void onTicketValidated(String message) {
        try {
            TicketValidatedEvent event = objectMapper
                    .readValue(message, TicketValidatedEvent.class);

            notificationService.create(Notification.builder()
                    .userId(event.getPassengerUserId())
                    .userType(UserType.PASSENGER)
                    .canal(NotificationCanal.PUSH)
                    .type(NotificationType.TICKET_VALIDATED)
                    .sujet("Ticket validé")
                    .contenu("Ticket validé à la station "
                            + event.getStationId()
                            + ". Bon voyage !")
                    .evenementSource("ticket.validated")
                    .referenceId(event.getTicketId())
                    .build());

        } catch (Exception e) {
            log.error("Erreur onTicketValidated : {}", e.getMessage());
        }
    }

    // ── payment.failed ─────────────────────────────────────────

    @KafkaListener(
            topics = "payment.failed",
            groupId = "notification-service-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void onPaymentFailed(String message) {
        try {
            PaymentFailedEvent event = objectMapper
                    .readValue(message, PaymentFailedEvent.class);

            notificationService.create(Notification.builder()
                    .userId(event.getPassengerUserId())
                    .userType(UserType.PASSENGER)
                    .canal(NotificationCanal.SMS)
                    .type(NotificationType.PAYMENT_FAILED)
                    .sujet("Paiement échoué")
                    .contenu("Votre paiement a échoué. Raison : "
                            + event.getReason()
                            + ". Veuillez réessayer.")
                    .evenementSource("payment.failed")
                    .referenceId(event.getPaymentId())
                    .build());

        } catch (Exception e) {
            log.error("Erreur onPaymentFailed : {}", e.getMessage());
        }
    }

    // ── subscription.created ───────────────────────────────────

    @KafkaListener(
            topics = "subscription.created",
            groupId = "notification-service-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void onSubscriptionCreated(String message) {
        try {
            SubscriptionCreatedEvent event = objectMapper
                    .readValue(message, SubscriptionCreatedEvent.class);

            notificationService.create(Notification.builder()
                    .userId(event.getPassengerUserId())
                    .userType(UserType.PASSENGER)
                    .canal(NotificationCanal.SMS)
                    .type(NotificationType.SUBSCRIPTION_CREATED)
                    .sujet("Abonnement activé")
                    .contenu("Votre abonnement "
                            + event.getType()
                            + " est activé jusqu'au "
                            + event.getDateFin() + ".")
                    .evenementSource("subscription.created")
                    .referenceId(event.getAbonnementId())
                    .build());

        } catch (Exception e) {
            log.error("Erreur onSubscriptionCreated : {}", e.getMessage());
        }
    }

    // ── user.registered → compte créé ─────────────────────────

    @KafkaListener(
            topics = "user.registered",
            groupId = "notification-service-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void onUserRegistered(String message) {
        try {
            UserRegisteredEvent event = objectMapper
                    .readValue(message, UserRegisteredEvent.class);

            UserType userType = event.getRole().equals("DRIVER")
                    ? UserType.DRIVER : UserType.PASSENGER;

            notificationService.create(Notification.builder()
                    .userId(event.getUserId())
                    .userType(userType)
                    .canal(NotificationCanal.EMAIL)
                    .type(NotificationType.ACCOUNT_CREATED)
                    .sujet("Bienvenue sur BRT Dakar")
                    .contenu("Bonjour "
                            + event.getFirstName()
                            + ", votre compte a été créé avec succès.")
                    .evenementSource("user.registered")
                    .referenceId(event.getUserId())
                    .build());

        } catch (Exception e) {
            log.error("Erreur onUserRegistered : {}", e.getMessage());
        }
    }
}
