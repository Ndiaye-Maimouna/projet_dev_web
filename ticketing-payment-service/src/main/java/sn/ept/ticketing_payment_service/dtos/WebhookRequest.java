package sn.ept.ticketing_payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebhookRequest {
    private String idPayment;    // id du payment
    private String status;       // SUCCESS ou FAILED
    private String operatorRef;  // référence côté opérateur
}
