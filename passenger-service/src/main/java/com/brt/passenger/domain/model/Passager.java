package com.brt.passenger.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entité Passager — alignée sur le diagramme de classes BRT.
 *
 * Diagramme :
 *   Passager --> Ticket     (1 → many)
 *   Passager --> Abonnement (1 → many)
 *   Passager --> Paiement   (1 → many)
 */
@Entity
@Table(
    name = "passagers",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_passager_email",     columnNames = "email"),
        @UniqueConstraint(name = "uk_passager_telephone", columnNames = "telephone")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passager {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Diagramme : +String nom */
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    /** Diagramme : +String prenom */
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    /** Diagramme : +String email */
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /** Diagramme : +String telephone */
    @Column(name = "telephone", nullable = false, unique = true, length = 20)
    private String telephone;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    @Builder.Default
    private PassagerStatus statut = PassagerStatus.ACTIVE;

    @Column(name = "cree_le", nullable = false, updatable = false)
    private Instant creeLe;

    @Column(name = "modifie_le", nullable = false)
    private Instant modifieLe;

    /** Diagramme : Passager --> Ticket (1 → many) */
    @OneToMany(mappedBy = "passager", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    /** Diagramme : Passager --> Abonnement (1 → many) */
    @OneToMany(mappedBy = "passager", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Abonnement> abonnements = new ArrayList<>();

    /** Diagramme : Passager --> Paiement (1 → many) */
    @OneToMany(mappedBy = "passager", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Paiement> paiements = new ArrayList<>();

    // ── Lifecycle ──────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.creeLe    = now;
        this.modifieLe = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifieLe = Instant.now();
    }

    // ── Méthodes métier (diagramme) ────────────────────────────────────

    /** Diagramme : +consulterHistorique() */
    public List<Ticket> consulterHistorique() {
        return List.copyOf(this.tickets);
    }

    public String getNomComplet() {
        return this.prenom + " " + this.nom;
    }

    public boolean isActive() {
        return this.statut == PassagerStatus.ACTIVE;
    }

    public void activer()    { this.statut = PassagerStatus.ACTIVE; }
    public void desactiver() { this.statut = PassagerStatus.INACTIVE; }
    public void suspendre()  { this.statut = PassagerStatus.SUSPENDED; }

    public boolean aUnAbonnementActif() {
        return abonnements.stream().anyMatch(Abonnement::isActif);
    }
}
