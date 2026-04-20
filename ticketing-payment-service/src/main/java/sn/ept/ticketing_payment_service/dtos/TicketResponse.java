package sn.ept.ticketing_payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private UUID id;
    private String passengerId;
    private String lineId;
    private String departureStationId;
    private String arrivalStationId;
    private BigDecimal prix;
    private String statut;
    private String qrCode;
    private LocalDateTime dateAchat;
    private LocalDateTime dateExpiration;
    private LocalDateTime validatedAt;
    private String validatedStationId;
    private String paymentId;
}
