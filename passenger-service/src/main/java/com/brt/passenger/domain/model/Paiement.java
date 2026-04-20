package com.brt.passenger.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Entité Paiement — diagramme de classes BRT.
 *
 * Attributs du diagramme :
 *   +String id
 *   +double montant
 *   +String methode
 *   +String statut
 *   +DateTime date
 *
 * Relations :
 *   Passager --> Paiement (1 → many)
 *   Ticket   --> Paiement (1 → 1) : "genere"
 */
@Entity
@Table(name = "paiements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Diagramme : +double montant */
    @Column(name = "montant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    /** Diagramme : +String methode (WAVE, ORANGE_MONEY, CB, ESPECES) */
    @Enumerated(EnumType.STRING)
    @Column(name = "methode", nullable = false, length = 30)
    private MethodePaiement methode;

    /** Diagramme : +String statut */
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    @Builder.Default
    private PaiementStatut statut = PaiementStatut.EN_ATTENTE;

    /** Diagramme : +DateTime date */
    @Column(name = "date_paiement", nullable = false, updatable = false)
    @Builder.Default
    private Instant date = Instant.now();

    /** Relation : Passager --> Paiement (many) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passager_id", nullable = false)
    private Passager passager;

    /**
     * Relation : Ticket --> Paiement (1 → 1)
     * Le paiement est généré par un ticket.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false, unique = true)
    private Ticket ticket;

    // ── Méthodes métier (diagramme) ────────────────────────────────────

    /** Diagramme : +traiter() */
    public void traiter() {
        if (this.statut != PaiementStatut.EN_ATTENTE) {
            throw new IllegalStateException("Le paiement ne peut être traité que s'il est en attente.");
        }
        this.statut = PaiementStatut.EFFECTUE;
    }

    /** Diagramme : +rembourser() */
    public void rembourser() {
        if (this.statut != PaiementStatut.EFFECTUE) {
            throw new IllegalStateException("Seul un paiement effectué peut être remboursé.");
        }
        this.statut = PaiementStatut.REMBOURSE;
    }
}
