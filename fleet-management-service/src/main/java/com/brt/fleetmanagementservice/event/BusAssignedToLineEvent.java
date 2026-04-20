package com.brt.fleetmanagementservice.event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class BusAssignedToLineEvent extends ApplicationEvent {

    private final UUID          affectationId;
    private final UUID          busId;
    private final UUID          conducteurId;
    private final UUID          ligneId;
    private final LocalDateTime dateDebut;
    private final LocalDateTime dateFin;

    public BusAssignedToLineEvent(Object source,
                                  UUID affectationId,
                                  UUID busId,
                                  UUID conducteurId,
                                  UUID ligneId,
                                  LocalDateTime dateDebut,
                                  LocalDateTime dateFin) {
        super(source);
        this.affectationId = affectationId;
        this.busId         = busId;
        this.conducteurId  = conducteurId;
        this.ligneId       = ligneId;
        this.dateDebut     = dateDebut;
        this.dateFin       = dateFin;
    }
}
