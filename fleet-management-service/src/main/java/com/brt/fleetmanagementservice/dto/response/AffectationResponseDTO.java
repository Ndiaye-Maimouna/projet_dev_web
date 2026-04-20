package com.brt.fleetmanagementservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AffectationResponseDTO {
    private UUID id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private long          dureeMinutes;
    private String        statut;
    private UUID          ligneId;
    // Conducteur
    private UUID          conducteurId;
    private String        conducteurNom;
    private String        conducteurPrenom;
    // Bus
    private UUID          busId;
    private String        busImmatriculation;
}