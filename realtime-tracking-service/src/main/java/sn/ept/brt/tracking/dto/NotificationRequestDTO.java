package sn.ept.brt.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO {
    private String to;
    private String type;
    private String subject;
    private String message;
    private String passengerId;
}