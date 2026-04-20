package sn.ept.brt.tracking.dto;

import lombok.Data;

@Data
public class BusDTO {
    private String id;
    private String immatriculation;
    private int capacite;
    private String statut;
    private String ligneId;
}