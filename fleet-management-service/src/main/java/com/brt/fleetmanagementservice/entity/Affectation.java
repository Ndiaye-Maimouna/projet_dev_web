package com.brt.fleetmanagementservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "affectations")
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Référence vers la ligne dans operation-service (pas de FK directe)
    private UUID ligneId;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    // ACTIVE | TERMINEE | ANNULEE
    private String statut;

    @ManyToOne
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Conducteur conducteur;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

}