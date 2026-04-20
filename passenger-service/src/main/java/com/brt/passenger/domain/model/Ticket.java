package com.brt.passenger.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Entité Ticket — diagramme de classes BRT.
 *
 * Attributs du diagramme :
 *   +String id
 *   +String statut
 *   +String id_Conducteur
 *   +DateTime date_achat
 *   +DateTime date_expiration
 *
 * Relations :
 *   Passager --> Ticket (many)
 *   Ticket   --> Paiement (1 → 1)
 */
@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Diagramme : +String statut */
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    @Builder.Default
    private TicketStatut statut = TicketStatut.ACTIF;

    /**
     * Diagramme : +String id_Conducteur
     * Référence externe vers fleet-management-service (pas de FK cross-service)
     */
    @Column(name = "id_conducteur", length = 36)
    private String idConducteur;

    /** Diagramme : +DateTime date_achat */
    @Column(name = "date_achat", nullable = false, updatable = false)
    @Builder.Default
    private Instant dateAchat = Instant.now();

    /** Diagramme : +DateTime date_expiration */
    @Column(name = "date_expiration", nullable = false)
    private Instant dateExpiration;

    /** Relation : Passager --> Ticket (many) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passager_id", nullable = false)
    private Passager passager;

    /**
     * Relation : Ticket --> Paiement (1 → 1)
     * Le paiement est généré par le ticket.
     */
    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Paiement paiement;

    // ── Méthodes métier (diagramme) ────────────────────────────────────

    /** Diagramme : +valider() */
    public void valider() {
        if (this.statut == TicketStatut.ANNULE) {
            throw new IllegalStateException("Impossible de valider un ticket annulé.");
        }
        if (Instant.now().isAfter(this.dateExpiration)) {
            throw new IllegalStateException("Le ticket est expiré.");
        }
        this.statut = TicketStatut.VALIDE;
    }

    /** Diagramme : +annuler() */
    public void annuler() {
        if (this.statut == TicketStatut.VALIDE) {
            throw new IllegalStateException("Impossible d'annuler un ticket déjà validé.");
        }
        this.statut = TicketStatut.ANNULE;
    }

    public boolean isExpire() {
        return Instant.now().isAfter(this.dateExpiration);
    }
}
