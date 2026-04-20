package com.brt.operationservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class StationRequestDTO {

    @NotBlank(message = "Le nom de la station est obligatoire.")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères.")
    private String nom;

    @DecimalMin(value = "-90.0", message = "Latitude invalide (min: -90).")
    @DecimalMax(value = "90.0",  message = "Latitude invalide (max: 90).")
    private double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude invalide (min: -180).")
    @DecimalMax(value = "180.0",  message = "Longitude invalide (max: 180).")
    private double longitude;

    private boolean actif = true;

    // Optionnel à la création, obligatoire pour association
    private UUID ligneId;
}