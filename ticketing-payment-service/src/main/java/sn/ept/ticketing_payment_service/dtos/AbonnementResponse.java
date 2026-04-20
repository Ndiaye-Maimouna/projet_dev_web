package sn.ept.ticketing_payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.ept.ticketing_payment_service.enums.AbonnementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AbonnementResponse {
    private UUID id;
    private String passengerId;
    private AbonnementType type;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal tarif;
    private boolean actif;
}
