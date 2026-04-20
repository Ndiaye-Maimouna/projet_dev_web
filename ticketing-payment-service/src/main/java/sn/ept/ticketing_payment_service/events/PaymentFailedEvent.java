package sn.ept.ticketing_payment_service.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailedEvent {
    private String paymentId;
    private String passengerId;
    private String passengerUserId;
    private String reason;
    private Instant timestamp;
}
