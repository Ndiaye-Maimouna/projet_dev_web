package sn.ept.ticketing_payment_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.ept.ticketing_payment_service.enums.PaymentStatus;
import sn.ept.ticketing_payment_service.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "passenger_id", nullable = false)
    private String passengerId;

    @Column(name = "ticket_id")
    private UUID ticketId;

    @Column(name = "abonnement_id")
    private UUID abonnementId;

    @Column(name = "montant", nullable = false)
    private BigDecimal montant;

    @Column(name = "devise", nullable = false)
    @Builder.Default
    private String devise = "XOF";

    @Enumerated(EnumType.STRING)
    @Column(name = "methode", nullable = false)
    private PaymentMethod methode;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    @Builder.Default
    private PaymentStatus statut = PaymentStatus.PENDING;

    @Column(name = "phone_number")
    private String phoneNumber;

    // Référence retournée par l'opérateur
    @Column(name = "operator_reference")
    private String operatorReference;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.date = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
