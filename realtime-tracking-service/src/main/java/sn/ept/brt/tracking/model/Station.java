package sn.ept.brt.tracking.model;

import lombok.Data;

@Data
public class Station {
    private String id;
    private String nom;
    private double latitude;
    private double longitude;
    private boolean actif;
}