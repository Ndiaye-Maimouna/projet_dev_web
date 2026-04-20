package com.brt.passenger.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TripHistoryResponse {
    private UUID        id;
    private UUID        ticketId;
    private String      lineId;
    private String      boardingStation;
    private String      alightingStation;
    private Instant     tripDate;
    private BigDecimal  amountPaid;
}
