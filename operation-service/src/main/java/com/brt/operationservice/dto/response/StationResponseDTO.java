package com.brt.operationservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StationResponseDTO {
    private UUID   id;
    private String nom;
    private double latitude;
    private double longitude;
    private boolean actif;

    private UUID   ligneId;
    private String ligneNom;
}