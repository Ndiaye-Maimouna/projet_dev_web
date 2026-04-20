package com.brt.passenger.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entité Abonnement — diagramme de classes BRT.
 *
 * Attributs du diagramme :
 *   +String id
 *   +String type
 *   +DateTime date_debut
 *   +DateTime date_fin
 *   +double tarif      ← nouveau par rapport à la v1
 *   +boolean actif     ← nouveau par rapport à la v1
 *
 * Relation : Passager --> Abonnement (1 → many)
 */
@Entity
@Table(name = "abonnements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Abonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Diagramme : +String type */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private SubscriptionType type;

    /** Diagramme : +DateTime date_debut */
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    /** Diagramme : +DateTime date_fin */
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    /** Diagramme : +double tarif */
    @Column(name = "tarif", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarif;

    /** Diagramme : +boolean actif */
    @Column(name = "actif", nullable = false)
    @Builder.Default
    private boolean actif = true;

    @Column(name = "cree_le", nullable = false, updatable = false)
    @Builder.Default
    private Instant creeLe = Instant.now();

    /** Relation : Passager --> Abonnement (many) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passager_id", nullable = false)
    private Passager passager;

    // ── Méthodes métier (diagramme) ────────────────────────────────────

    /** Diagramme : +renouveler() — prolonge de la durée du type */
    public void renouveler() {
        if (!this.actif) {
            throw new IllegalStateException("Impossible de renouveler un abonnement suspendu.");
        }
        LocalDate base = LocalDate.now().isAfter(this.dateFin) ? LocalDate.now() : this.dateFin;
        this.dateFin = switch (this.type) {
            case WEEKLY  -> base.plusWeeks(1);
            case MONTHLY -> base.plusMonths(1);
            case ANNUAL  -> base.plusYears(1);
        };
    }

    /** Diagramme : +suspendre() */
    public void suspendre() {
        this.actif = false;
    }

    /** Réactivation (complément logique de suspendre) */
    public void reactiver() {
        this.actif = true;
    }

    /** Diagramme : +boolean actif — vérifie date + flag */
    public boolean isActif() {
        return this.actif && !LocalDate.now().isAfter(this.dateFin);
    }
}
