package com.brt.operationservice.dto.request;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BusRequestDto {

    @NotBlank(message = "L'immatriculation est obligatoire.")
    @Pattern(
            regexp = "^[A-Z0-9\\-]{5,15}$",
            message = "Immatriculation invalide (ex: DK-1234-AB)."
    )
    private String immatriculation;

    @Min(value = 20, message = "La capacité minimale est de 20 passagers.")
    @Max(value = 300, message = "La capacité maximale est de 300 passagers.")
    private int capacite;

    @NotBlank(message = "Le statut est obligatoire.")
    @Pattern(
            regexp = "DISPONIBLE|EN_SERVICE|EN_MAINTENANCE|AFFECTE",
            message = "Statut invalide. Valeurs: DISPONIBLE, EN_SERVICE, EN_MAINTENANCE, AFFECTE."
    )
    private String statut;
}