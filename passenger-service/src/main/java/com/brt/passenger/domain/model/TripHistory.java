package com.brt.passenger.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Historique des trajets d'un passager.
 *
 * Alimenté via les événements Kafka (TicketValidated)
 * venant du ticketing-service.
 */
@Entity
@Table(name = "trip_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passager passager;

    /** Référence externe vers le ticket dans ticketing-service */
    @Column(name = "ticket_id")
    private UUID ticketId;

    @Column(name = "line_id", length = 50)
    private String lineId;

    @Column(name = "boarding_station", length = 100)
    private String boardingStation;

    @Column(name = "alighting_station", length = 100)
    private String alightingStation;

    @Column(name = "trip_date", nullable = false)
    @Builder.Default
    private Instant tripDate = Instant.now();

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid;
}
