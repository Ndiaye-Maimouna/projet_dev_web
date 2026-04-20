package sn.ept.ticketing_payment_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.ept.ticketing_payment_service.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "passenger_id", nullable = false)
    private String passengerId;

    @Column(name = "line_id", nullable = false)
    private String lineId;

    @Column(name = "departure_station_id", nullable = false)
    private String departureStationId;

    @Column(name = "arrival_station_id", nullable = false)
    private String arrivalStationId;


    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    @Builder.Default
    private TicketStatus statut = TicketStatus.PENDING_PAYMENT;

    @Column(name = "date_achat")
    private LocalDateTime dateAchat;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    @Column(name = "prix", nullable = false)
    private BigDecimal prix;

    @Column(name = "qr_code", unique = true)
    private String qrCode;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "validated_station_id")
    private String validatedStationId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
