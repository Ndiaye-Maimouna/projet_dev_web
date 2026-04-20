package sn.ept.security_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.ept.security_service.dtos.*;
import sn.ept.security_service.services.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register/passenger")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody PassengerRegisterRequest passengerRegisterRequest
    ){
        return ResponseEntity.ok(authenticationService.register(passengerRegisterRequest));
    }

    @PostMapping("/register/driver")
    public ResponseEntity<AuthenticationResponse> registerDriver(
            @RequestBody DriverRegisterRequest request,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .build();
        }
        return ResponseEntity.ok(authenticationService.registerDriver(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest loginRequest
    ){
        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader
    ) {
        authenticationService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest request,
            @RequestHeader("X-User-Email") String email
    ) {
        authenticationService.changePassword(email, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateInfo(
            @RequestBody UpdateInfoRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Email") String email
    ) {
        return ResponseEntity.ok(authenticationService.updateInfo(email, request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @RequestHeader("X-User-Email") String email
    ) {
        return ResponseEntity.ok(authenticationService.getMe(email));
    }
}
