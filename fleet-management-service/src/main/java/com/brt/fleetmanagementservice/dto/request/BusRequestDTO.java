package com.brt.fleetmanagementservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

@Data
public class BusRequestDTO {

    @NotBlank
    @Pattern(
            regexp = "^[A-Z0-9\\-]{5,15}$")
    private String immatriculation;

    @Min(20) @Max(300)
    private int capacite;

    @NotBlank
    private String marque;

    @NotBlank
    private String modele;

    @NotNull
    @PastOrPresent
    private LocalDate dateAcquisition;

    @PositiveOrZero
    private double kilometrage;

    @NotBlank
    @Pattern(regexp = "DISPONIBLE|EN_SERVICE|EN_MAINTENANCE|HORS_SERVICE")
    private String statut;
}
