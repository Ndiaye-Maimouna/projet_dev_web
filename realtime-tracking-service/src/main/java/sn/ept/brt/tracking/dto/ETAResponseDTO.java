package sn.ept.brt.tracking.dto;

import lombok.Data;

@Data
public class ETAResponseDTO {
    private String ligneId;
    private String stationId;
    private int minutes_restants;
    private int retard_minutes;
    private String prochainBusId;
}