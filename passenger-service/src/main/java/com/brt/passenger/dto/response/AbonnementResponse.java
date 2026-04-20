package com.brt.passenger.dto.response;

import com.brt.passenger.domain.model.SubscriptionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AbonnementResponse {
    private UUID           id;
    private SubscriptionType type;
    private LocalDate      dateDebut;
    private LocalDate      dateFin;
    private BigDecimal     tarif;
    private boolean        actif;
}
