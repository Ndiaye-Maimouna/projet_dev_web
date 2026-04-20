package com.brt.operationservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LigneBRTRequestDto {

    @NotBlank(message = "Le nom de la ligne est obligatoire.")
    @Size(min = 1, max = 50, message = "Le nom doit contenir entre 1 et 50 caractères.")
    private String nom;

    @NotBlank(message = "Le terminus de départ est obligatoire.")
    private String terminusDepart;

    @NotBlank(message = "Le terminus d'arrivée est obligatoire.")
    private String terminusArrivee;

    @Min(value = 1, message = "La fréquence doit être d'au moins 1 minute.")
    @Max(value = 120, message = "La fréquence ne peut pas dépasser 120 minutes.")
    private int frequenceMinutes;
}