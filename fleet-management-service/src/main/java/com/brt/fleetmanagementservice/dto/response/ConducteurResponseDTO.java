package com.brt.fleetmanagementservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ConducteurResponseDTO {
    private UUID id;
    private String    nom;
    private String    prenom;
    private String    numeroPermis;
    private String    telephone;
    private String    email;
    private LocalDate dateEmbauche;
    private String    statut;
    private int       nombreAffectations;
}