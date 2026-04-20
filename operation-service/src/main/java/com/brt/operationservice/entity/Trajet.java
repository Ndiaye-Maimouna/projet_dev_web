package com.brt.operationservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "trajets")
public class Trajet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime heureDepart;
    private LocalDateTime heureArrivee;
    private String statut;

    @ManyToOne
    private LigneBRT ligne;

    @ManyToOne
    @JoinColumn(name = "station_depart_id")
    private Station stationDepart;

    @ManyToOne
    @JoinColumn(name = "station_arrivee_id")
    private Station stationArrivee;
    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    public void calculerDuree() {}
    public void mettreAJourStatut() {}
}
