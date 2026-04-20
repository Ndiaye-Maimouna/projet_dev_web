package com.brt.notificationservice.entities;

import com.brt.notificationservice.enums.NotificationCanal;
import com.brt.notificationservice.enums.NotificationStatut;
import com.brt.notificationservice.enums.NotificationType;
import com.brt.notificationservice.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Qui reçoit la notification
    private String userId;          // id venant de security-service
    private String destinataire;    // email ou numéro de téléphone

    @Enumerated(EnumType.STRING)
    private UserType userType;      // PASSENGER, DRIVER, ADMIN

    @Enumerated(EnumType.STRING)
    private NotificationCanal canal; // EMAIL, SMS, PUSH

    @Enumerated(EnumType.STRING)
    private NotificationType type;   // TICKET_PURCHASED, TICKET_VALIDATED,
    // PAYMENT_FAILED, SUBSCRIPTION_CREATED,
    // DRIVER_ASSIGNED, MAINTENANCE_SCHEDULED

    private String sujet;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    private NotificationStatut statut; // ENVOYE, ECHEC, EN_ATTENTE

    private LocalDateTime dateEnvoi;
    private LocalDateTime createdAt;

    // Référence à l'événement source
    private String evenementSource;
    private String referenceId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.statut = NotificationStatut.EN_ATTENTE;
    }
}