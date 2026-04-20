package sn.ept.brt.tracking.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusDelayDetected {
    private String busId;
    private String ligneId;
    private int retard_minutes;
    private LocalDateTime timestamp;
    private String cause;
}