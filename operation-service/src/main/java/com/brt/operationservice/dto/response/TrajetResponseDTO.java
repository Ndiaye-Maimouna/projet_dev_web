package com.brt.operationservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TrajetResponseDTO {
    private UUID          id;
    private LocalDateTime heureDepart;
    private LocalDateTime heureArrivee;
    private String        statut;
    private long          dureeMinutes;

    private UUID   ligneId;
    private String ligneNom;

    private UUID   stationDepartId;
    private String stationDepartNom;

    private UUID   stationArriveeId;
    private String stationArriveeNom;

    private UUID   busId;
    private String busImmatriculation;
}