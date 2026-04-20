package sn.ept.brt.tracking.dto;

import lombok.Data;

@Data
public class StationDTO {
    private String id;
    private String nom;
    private double latitude;
    private double longitude;
    private boolean actif;
}