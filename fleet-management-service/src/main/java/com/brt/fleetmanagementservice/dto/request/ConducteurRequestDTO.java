package com.brt.fleetmanagementservice.dto.request;

import com.brt.fleetmanagementservice.enums.ConducteurStatut;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ConducteurRequestDTO {

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotBlank
    private String numeroPermis;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{8,15}$")
    private String telephone;

    @Email
    private String email;

    @NotNull
    @PastOrPresent
    private LocalDate dateEmbauche;

    @NotBlank
    @Pattern(regexp = "DISPONIBLE|EN_SERVICE|EN_CONGE|SUSPENDU")
    private ConducteurStatut statut;
}