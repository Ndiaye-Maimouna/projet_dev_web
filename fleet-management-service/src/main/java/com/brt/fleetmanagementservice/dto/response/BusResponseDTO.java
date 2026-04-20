package com.brt.fleetmanagementservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class BusResponseDTO {
    private UUID id;
    private String    immatriculation;
    private int       capacite;
    private String    marque;
    private String    modele;
    private LocalDate dateAcquisition;
    private double    kilometrage;
    private String    statut;
    private int       nombreAffectations;
}
