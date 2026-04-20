package sn.ept.ticketing_payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.ept.ticketing_payment_service.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private UUID id;
    private String passengerId;
    private UUID ticketId;
    private UUID abonnementId;
    private BigDecimal montant;
    private String devise;
    private PaymentMethod methode;
    private String statut;
    private String phoneNumber;
    private String operatorReference;
    private LocalDateTime date;
    private LocalDateTime createdAt;
}
