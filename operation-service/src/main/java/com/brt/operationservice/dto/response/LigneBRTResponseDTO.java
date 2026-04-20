package com.brt.operationservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LigneBRTResponseDTO {
    private UUID   id;
    private String nom;
    private String terminusDepart;
    private String terminusArrivee;
    private int    frequenceMinutes;

    private int    nombreStations;
    private int    nombreBus;
    private List<StationResponseDTO> stations;
    private List<BusResponseDTO>     bus;
}