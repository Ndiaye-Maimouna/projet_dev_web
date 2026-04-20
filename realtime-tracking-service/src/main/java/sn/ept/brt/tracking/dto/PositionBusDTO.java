package sn.ept.brt.tracking.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PositionBusDTO {
    private String busId;
    private double latitude;
    private double longitude;
    private int vitesse_kmh;
    private LocalDateTime timestamp;
}