package com.brt.fleetmanagementservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AffectationRequestDTO {

    @NotNull
    private UUID conducteurId;

    @NotNull
    private UUID busId;

    // Référence externe vers operation-service
    @NotNull
    private UUID ligneId;

    @NotNull
    private LocalDateTime dateDebut;

    @NotNull
    private LocalDateTime dateFin;

    @NotBlank
    @Pattern(regexp = "ACTIVE|TERMINEE|ANNULEE")
    private String statut;
}