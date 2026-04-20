package sn.ept.brt.tracking.dto;

import lombok.Data;
import java.util.List;

@Data
public class LigneBRTDTO {
    private String id;
    private String nom;
    private String terminus_depart;
    private String terminus_arrivee;
    private int frequence_minutes;
    private List<StationDTO> stations;
}