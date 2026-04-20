package sn.ept.brt.tracking.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusArrivedAtStation {
    private String busId;
    private String stationId;
    private String ligneId;
    private LocalDateTime timestamp;
    private int retard_minutes;
}