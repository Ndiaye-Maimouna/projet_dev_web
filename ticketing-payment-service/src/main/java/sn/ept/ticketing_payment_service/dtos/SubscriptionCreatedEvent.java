package sn.ept.ticketing_payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionCreatedEvent {
    private String abonnementId;
    private String passengerId;
    private String type;
    private String dateFin;
    private Instant timestamp;
}
