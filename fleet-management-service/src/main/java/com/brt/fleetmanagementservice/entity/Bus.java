package com.brt.fleetmanagementservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bus")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String immatriculation;

    private int capacite;

    private String marque;

    private String modele;

    private LocalDate dateAcquisition;

    private double kilometrage;

    // DISPONIBLE | EN_SERVICE | EN_MAINTENANCE | HORS_SERVICE
    private String statut;


    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL)
    private List<Affectation> affectations;
}