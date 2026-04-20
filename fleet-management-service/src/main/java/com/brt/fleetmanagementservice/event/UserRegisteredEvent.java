package com.brt.fleetmanagementservice.event;

import com.brt.fleetmanagementservice.enums.ConducteurStatut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisteredEvent {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private String licenseNumber;
}