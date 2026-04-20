package sn.ept.security_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverRegisterRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String licenseNumber;
}