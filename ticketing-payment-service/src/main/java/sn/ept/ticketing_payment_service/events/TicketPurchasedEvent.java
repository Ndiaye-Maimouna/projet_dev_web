package sn.ept.ticketing_payment_service.events;

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
public class TicketPurchasedEvent {
    private String ticketId;
    private String passengerId;
    private String passengerUserId;
    private String lineId;
    private BigDecimal amount;
    private Instant timestamp;
}
