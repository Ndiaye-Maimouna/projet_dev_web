package sn.ept.ticketing_payment_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.ept.ticketing_payment_service.enums.AbonnementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "abonnements")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Abonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "passenger_id", nullable = false)
    private String passengerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AbonnementType type;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "tarif", nullable = false)
    private BigDecimal tarif;

    @Column(name = "actif", nullable = false)
    @Builder.Default
    private boolean actif = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Méthodes métier du diagramme
    public void renouveler() {
        this.dateDebut = this.dateFin;
        this.dateFin = switch (this.type) {
            case AbonnementType.WEEKLY  -> this.dateDebut.plusWeeks(1);
            case AbonnementType.MONTHLY -> this.dateDebut.plusMonths(1);
            case AbonnementType.ANNUAL  -> this.dateDebut.plusYears(1);
        };
        this.actif = true;
    }

    public void suspendre() {
        this.actif = false;
    }

    public boolean isValid() {
        return this.actif && LocalDate.now().isBefore(this.dateFin);
    }
}
