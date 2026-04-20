package com.brt.fleetmanagementservice.event;

import com.brt.fleetmanagementservice.entity.Conducteur;
import com.brt.fleetmanagementservice.enums.ConducteurStatut;
import com.brt.fleetmanagementservice.repository.ConducteurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final ConducteurRepository conducteurRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "user.registered",
            groupId = "fleet-user-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void onUserRegistered(String message) {
        try {
            UserRegisteredEvent event = objectMapper
                    .readValue(message, UserRegisteredEvent.class);

            if (!event.getRole().equals("DRIVER")) return;

            Conducteur conducteur = Conducteur.builder()
                    .userId(event.getUserId())
                    .email(event.getEmail())
                    .nom(event.getLastName())
                    .prenom(event.getFirstName())
                    .telephone(event.getPhone())
                    .dateEmbauche(LocalDate.now())
                    .statut(ConducteurStatut.DISPONIBLE)
                    .numeroPermis(event.getLicenseNumber())
                    .build();

            conducteurRepository.save(conducteur);
            log.info("Conducteur créé : {}", event.getEmail());

        } catch (Exception e) {
            log.error("Erreur création conducteur : {}", e.getMessage());
        }
    }
}
