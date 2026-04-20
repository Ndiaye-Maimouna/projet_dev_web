package com.brt.notificationservice.kafka;

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
public class TicketValidatedEvent {
    private String ticketId;
    private String passengerId;
    private String passengerUserId;
    private String stationId;
    private String lineId;
    private BigDecimal amount;
    private Instant timestamp;
}
