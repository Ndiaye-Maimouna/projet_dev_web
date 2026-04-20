package com.brt.passenger.dto.response;

import com.brt.passenger.domain.model.PassagerStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de réponse pour un passager.
 * Ne contient jamais de données sensibles (mot de passe, etc.).
 */
@Data
@Builder
public class PassengerResponse {
    private UUID            id;
    private UUID passengerUserId;
    private String          firstName;
    private String          lastName;
    private String          fullName;
    private String          email;
    private String          phoneNumber;
    private PassagerStatus status;
    private boolean         hasActiveSubscription;
    private Instant         createdAt;
    private Instant         updatedAt;
}
