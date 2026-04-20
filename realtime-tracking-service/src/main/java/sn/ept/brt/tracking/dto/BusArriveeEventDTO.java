package sn.ept.brt.tracking.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusArriveeEventDTO {
    private String busId;
    private String stationId;
    private String ligneId;
    private LocalDateTime heure_arrivee;
    private int retard_minutes;
}