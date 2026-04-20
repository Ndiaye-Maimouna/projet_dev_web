package com.brt.operationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lignes_brt")
public class LigneBRT {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nom;
    @Column(name = "terminus_depart")
    private String terminusDepart;

    @Column(name = "terminus_arrivee")
    private String terminusArrivee;
    private int frequence_minutes;

    @OneToMany(mappedBy = "ligne")
    private List<Station> stations;

    @OneToMany(mappedBy = "ligne")
    private List<Trajet> trajets;

    @ManyToMany
    private List<Bus> bus;

    // Méthodes métier
    public void ajouterStation() {}
    public void calculerDuree() {}
}