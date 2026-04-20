package sn.ept.ticketing_payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerResponse {
    private UUID id;
    private UUID passengerUserId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private PassagerStatus status;
    private boolean hasActiveSubscription;
    private Instant createdAt;
    private Instant updatedAt;
}