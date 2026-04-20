package com.brt.passenger.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de création — aligné sur le diagramme de classes.
 * Attributs : nom, prenom, email, telephone
 */
@Data
public class CreatePassagerRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 100)
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{7,14}$",
            message = "Numéro invalide (ex: +221771234567)"
    )
    private String telephone;
}
