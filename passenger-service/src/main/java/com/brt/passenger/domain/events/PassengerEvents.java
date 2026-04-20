package com.brt.passenger.domain.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Événements de domaine publiés par le passenger-service sur Kafka.
 *
 * Ces classes sont des records immuables transportant l'état
 * nécessaire aux autres services pour réagir.
 */
public final class PassengerEvents {

    private PassengerEvents() {}

    // ── PassengerRegistered ────────────────────────────────────────────

    /**
     * Émis quand un nouveau passager s'inscrit.
     * Consommateurs typiques : auth-service, notification-service
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassengerRegistered {
        private UUID        passengerId;
        private String      firstName;
        private String      lastName;
        private String      email;
        private String      phoneNumber;
        private Instant     registeredAt;
        private String      eventType = "PassengerRegistered";
    }

    // ── PassengerUpdated ───────────────────────────────────────────────

    /**
     * Émis quand le profil d'un passager est modifié.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassengerUpdated {
        private UUID        passengerId;
        private String      email;
        private String      phoneNumber;
        private Instant     updatedAt;
        private String      eventType = "PassengerUpdated";
    }

    // ── PassengerDeactivated ───────────────────────────────────────────

    /**
     * Émis quand un compte est désactivé ou suspendu.
     * Consommateurs : ticketing-service (invalide les abonnements actifs)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassengerDeactivated {
        private UUID        passengerId;
        private String      reason;
        private Instant     deactivatedAt;
        private String      eventType = "PassengerDeactivated";
    }

    // ── PassengerEnteredStation ────────────────────────────────────────

    /**
     * Émis quand un passager entre dans une station (via validation ticket).
     * Défini dans le document d'architecture.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassengerEnteredStation {
        private UUID        passengerId;
        private String      stationId;
        private Instant     timestamp;
        private String      eventType = "PassengerEnteredStation";
    }
}
