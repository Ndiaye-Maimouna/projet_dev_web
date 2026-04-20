package sn.ept.brt.tracking.model;

import lombok.Data;
import java.util.List;

@Data
public class LigneBRT {
    private String id;
    private String nom;
    private String terminus_depart;
    private String terminus_arrivee;
    private int frequence_minutes;
    private List<Station> stations;
    private List<Bus> bus;
}