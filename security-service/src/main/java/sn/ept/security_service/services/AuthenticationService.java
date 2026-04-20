package sn.ept.security_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.ept.security_service.dtos.*;
import sn.ept.security_service.entities.User;
import sn.ept.security_service.enums.Role;
import sn.ept.security_service.exceptions.BusinessException;
import sn.ept.security_service.exceptions.ResourceNotFoundException;
import sn.ept.security_service.repositories.UserRepository;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;
    private final TokenBlacklistService blacklistService;

    public AuthenticationResponse register(PassengerRegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email déjà utilisé" + request.getEmail());
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PASSENGER)
                .build();
        userRepository.save(user);

        publishPassengerRegistered(user, request);

        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    public AuthenticationResponse registerDriver(DriverRegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email déjà utilisé : "
                    + request.getEmail());
        }

        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new BusinessException("Le prénom est obligatoire");
        }

        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new BusinessException("Le nom est obligatoire");
        }

        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new BusinessException("Le téléphone est obligatoire");
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.DRIVER)
                .build();
        userRepository.save(user);

        publishDriverRegistered(user, request);

        log.info("Conducteur créé : {}", user.getEmail());

        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new BusinessException("Email ou mot de passe incorrect");
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable : " + request.getEmail()));

        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException("Header Authorization invalide");
        }
        String token = authHeader.substring(7);
        long expirationMs = jwtService.getExpirationMs(token);
        if (expirationMs > 0) {
            blacklistService.blacklist(token, expirationMs);
            log.info("Utilisateur déconnecté");
        }
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe actuel incorrect");
        }

        if (request.getNewPassword() == null
                || request.getNewPassword().length() < 8) {
            throw new BusinessException(
                    "Le nouveau mot de passe doit faire au moins 8 caractères");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserResponse updateInfo(String email, UpdateInfoRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        kafkaTemplate.send("user.updated", UserRegisteredEvent.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(user.getRole().name())
                .build());

        return UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public UserResponse getMe(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        return UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    private void publishPassengerRegistered(User user, PassengerRegisterRequest request) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(user.getRole().name())
                .build();

        kafkaTemplate.send("user.registered", event);
        log.info("UserRegistered publié pour : {}", user.getEmail());
    }

    private void publishDriverRegistered(User user, DriverRegisterRequest request) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(user.getRole().name())
                .licenseNumber(request.getLicenseNumber())
                .build();

        kafkaTemplate.send("user.registered", event);
        log.info("UserRegistered publié pour : {}", user.getEmail());
    }
}