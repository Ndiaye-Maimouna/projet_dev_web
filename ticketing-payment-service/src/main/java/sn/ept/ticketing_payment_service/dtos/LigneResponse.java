package sn.ept.ticketing_payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LigneResponse {
    private String id;
    private String nom;
    private String terminusDepart;
    private String terminusArrivee;
    private int frequenceMinutes;
    private int    nombreStations;
    private int    nombreBus;
}
