package sn.ept.brt.tracking.dto;

import lombok.Data;

@Data
public class ConducteurDTO {
    private String id_Conducteur;
    private String nom;
    private String prenom;
    private String numero_permis;
    private String statut;
}