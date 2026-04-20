package com.brt.operationservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "bus")
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String immatriculation;
    private int capacite;
    private String statut;


    public void verifierDisponibilite() {}
    public void planifierMaintenance() {}
}