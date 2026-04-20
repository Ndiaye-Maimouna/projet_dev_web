package sn.ept.ticketing_payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.ept.ticketing_payment_service.enums.PaymentMethod;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AbonnementRequest {
    private String type;           // WEEKLY, MONTHLY, ANNUAL
    private PaymentMethod paymentMethod;  // WAVE, ORANGE_MONEY, FREE_MONEY, CASH
}
