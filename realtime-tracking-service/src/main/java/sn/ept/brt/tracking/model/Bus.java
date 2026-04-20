package sn.ept.brt.tracking.model;

import lombok.Data;

@Data
public class Bus {
    private String id;
    private String immatriculation;
    private int capacite;
    private String statut;
    private LigneBRT ligne;
    private SuiviTempsReel suivi;
}