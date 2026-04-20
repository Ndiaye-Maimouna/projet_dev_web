package sn.ept.brt.tracking.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SuiviTempsReel {

    private String id;
    private double latitude;
    private double longitude;
    private int vitesse_kmh;
    private int retard_minutes;
    private LocalDateTime derniere_maj;
    private Bus bus;

    public void mettreAJourPosition(double lat, double lon, int vitesse) {
        this.latitude = lat;
        this.longitude = lon;
        this.vitesse_kmh = vitesse;
        this.derniere_maj = LocalDateTime.now();
    }

    public void detecterRetard(int retard) {
        this.retard_minutes = retard;
    }
}