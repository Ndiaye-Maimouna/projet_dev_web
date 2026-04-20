package com.brt.passenger.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de mise à jour partielle d'un passager.
 * Tous les champs sont optionnels (PATCH sémantique).
 */
@Data
public class UpdatePassengerRequest {

    @Size(min = 2, max = 100)
    private String firstName;

    @Size(min = 2, max = 100)
    private String lastName;

    @Email(message = "Format d'email invalide")
    private String email;

    @Pattern(
        regexp = "^\\+?[1-9]\\d{7,14}$",
        message = "Numéro de téléphone invalide"
    )
    private String phoneNumber;
}
