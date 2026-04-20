package com.brt.operationservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TrajetRequestDTO {

    @NotNull(message = "L'heure de départ est obligatoire.")
    @Future(message = "L'heure de départ doit être dans le futur.")
    private LocalDateTime heureDepart;

    @NotNull(message = "L'heure d'arrivée est obligatoire.")
    private LocalDateTime heureArrivee;

    @NotBlank(message = "Le statut est obligatoire.")
    @Pattern(
            regexp = "PLANIFIE|EN_COURS|TERMINE|ANNULE",
            message = "Statut invalide. Valeurs: PLANIFIE, EN_COURS, TERMINE, ANNULE."
    )
    private String statut;

    @NotNull(message = "L'identifiant de la ligne est obligatoire.")
    private UUID ligneId;

    @NotNull(message = "La station de départ est obligatoire.")
    private UUID stationDepartId;

    @NotNull(message = "La station d'arrivée est obligatoire.")
    private UUID stationArriveeId;

    @NotNull(message = "L'identifiant du bus est obligatoire.")
    private UUID busId;
}