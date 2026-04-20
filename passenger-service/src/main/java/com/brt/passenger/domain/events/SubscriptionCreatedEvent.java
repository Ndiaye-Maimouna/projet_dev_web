package com.brt.passenger.domain.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionCreatedEvent {
    private String abonnementId;
    private String passengerId;
    private String passengerUserId;
    private String type;
    private String dateFin;
    private Instant timestamp;
    private BigDecimal price;
}
