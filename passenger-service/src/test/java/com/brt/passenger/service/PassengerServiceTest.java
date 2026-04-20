package com.brt.passenger.service;

import com.brt.passenger.domain.model.Passager;
import com.brt.passenger.domain.model.PassagerStatus;
import com.brt.passenger.dto.request.CreatePassagerRequest;
import com.brt.passenger.dto.request.UpdatePassengerRequest;
import com.brt.passenger.dto.response.PassengerResponse;
import com.brt.passenger.exception.DuplicatePassengerException;
import com.brt.passenger.exception.PassengerNotFoundException;
import com.brt.passenger.kafka.PassengerEventProducer;
import com.brt.passenger.repository.AbonnementRepository;
import com.brt.passenger.repository.PassengerRepository;
import com.brt.passenger.repository.TripHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PassengerService - Tests unitaires")
class PassengerServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private TripHistoryRepository tripHistoryRepository;

    @Mock
    private AbonnementRepository abonnementRepository;

    @Mock
    private PassengerEventProducer eventProducer;

    @InjectMocks
    private PassengerService passengerService;

    private Passager samplePassager;
    private UUID     sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        samplePassager = Passager.builder()
                .id(sampleId)
                .prenom("Fatou")
                .nom("Diallo")
                .email("fatou.diallo@brt.sn")
                .telephone("+221771234567")
                .statut(PassagerStatus.ACTIVE)
                .build();
    }

    // ── creerPassager ──────────────────────────────────────────────────

    @Test
    @DisplayName("Création d'un passager valide → succès + événement Kafka publié")
    void creerPassager_success() {
        CreatePassagerRequest request = new CreatePassagerRequest();
        request.setNom("Diallo");
        request.setPrenom("Fatou");
        request.setEmail("fatou.diallo@brt.sn");
        request.setTelephone("+221771234567");

        when(passengerRepository.existsByEmail(anyString())).thenReturn(false);
        when(passengerRepository.existsByTelephone(anyString())).thenReturn(false);
        when(passengerRepository.save(any(Passager.class))).thenReturn(samplePassager);

        PassengerResponse response = passengerService.creerPassager(request);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("fatou.diallo@brt.sn");
        assertThat(response.getStatus()).isEqualTo(PassagerStatus.ACTIVE);

        verify(passengerRepository).save(any(Passager.class));
        verify(eventProducer).publishPassengerRegistered(any());
    }

    @Test
    @DisplayName("Création avec email existant → DuplicatePassengerException")
    void creerPassager_duplicateEmail_throwsException() {
        CreatePassagerRequest request = new CreatePassagerRequest();
        request.setNom("Diallo");
        request.setPrenom("Fatou");
        request.setEmail("fatou.diallo@brt.sn");
        request.setTelephone("+221771234567");

        when(passengerRepository.existsByEmail("fatou.diallo@brt.sn")).thenReturn(true);

        assertThatThrownBy(() -> passengerService.creerPassager(request))
                .isInstanceOf(DuplicatePassengerException.class)
                .hasMessageContaining("email");

        verify(passengerRepository, never()).save(any());
        verify(eventProducer, never()).publishPassengerRegistered(any());
    }

    @Test
    @DisplayName("Création avec téléphone existant → DuplicatePassengerException")
    void creerPassager_duplicateTelephone_throwsException() {
        CreatePassagerRequest request = new CreatePassagerRequest();
        request.setNom("Ndiaye");
        request.setPrenom("Mamadou");
        request.setEmail("mamadou@brt.sn");
        request.setTelephone("+221771234567");

        when(passengerRepository.existsByEmail(anyString())).thenReturn(false);
        when(passengerRepository.existsByTelephone("+221771234567")).thenReturn(true);

        assertThatThrownBy(() -> passengerService.creerPassager(request))
                .isInstanceOf(DuplicatePassengerException.class)
                .hasMessageContaining("téléphone");

        verify(passengerRepository, never()).save(any());
        verify(eventProducer, never()).publishPassengerRegistered(any());
    }

    // ── getPassagerById ────────────────────────────────────────────────

    @Test
    @DisplayName("Récupération par ID existant → retourne le passager")
    void getPassagerById_found() {
        when(passengerRepository.findById(sampleId)).thenReturn(Optional.of(samplePassager));

        PassengerResponse response = passengerService.getPassagerById(sampleId);

        assertThat(response.getId()).isEqualTo(sampleId);
        assertThat(response.getFullName()).isEqualTo("Fatou Diallo");
    }

    @Test
    @DisplayName("Récupération par ID inexistant → PassengerNotFoundException")
    void getPassagerById_notFound() {
        UUID unknownId = UUID.randomUUID();
        when(passengerRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passengerService.getPassagerById(unknownId))
                .isInstanceOf(PassengerNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }

    // ── updatePassager ─────────────────────────────────────────────────

    @Test
    @DisplayName("Mise à jour du prénom → enregistré et événement publié")
    void updatePassager_prenom_success() {
        UpdatePassengerRequest request = new UpdatePassengerRequest();
        request.setFirstName("Fatoumata");

        when(passengerRepository.findById(sampleId)).thenReturn(Optional.of(samplePassager));
        when(passengerRepository.save(any(Passager.class))).thenReturn(samplePassager);

        passengerService.updatePassager(sampleId, request);

        verify(passengerRepository).save(any(Passager.class));
        verify(eventProducer).publishPassengerUpdated(any());
    }

    @Test
    @DisplayName("Mise à jour avec email dupliqué → DuplicatePassengerException")
    void updatePassager_duplicateEmail_throwsException() {
        UpdatePassengerRequest request = new UpdatePassengerRequest();
        request.setEmail("autre@brt.sn");

        when(passengerRepository.findById(sampleId)).thenReturn(Optional.of(samplePassager));
        when(passengerRepository.existsByEmail("autre@brt.sn")).thenReturn(true);

        assertThatThrownBy(() -> passengerService.updatePassager(sampleId, request))
                .isInstanceOf(DuplicatePassengerException.class)
                .hasMessageContaining("email");

        verify(passengerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Mise à jour d'un ID inexistant → PassengerNotFoundException")
    void updatePassager_notFound() {
        UUID unknownId = UUID.randomUUID();
        when(passengerRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passengerService.updatePassager(unknownId, new UpdatePassengerRequest()))
                .isInstanceOf(PassengerNotFoundException.class);

        verify(passengerRepository, never()).save(any());
        verify(eventProducer, never()).publishPassengerUpdated(any());
    }

    // ── supprimerPassager ──────────────────────────────────────────────

    @Test
    @DisplayName("Suppression d'un passager existant → supprimé + événement publié")
    void supprimerPassager_success() {
        when(passengerRepository.findById(sampleId)).thenReturn(Optional.of(samplePassager));
        doNothing().when(passengerRepository).delete(samplePassager);

        passengerService.supprimerPassager(sampleId);

        verify(passengerRepository).delete(samplePassager);
        verify(eventProducer).publishPassengerDeactivated(any());
    }

    @Test
    @DisplayName("Suppression d'un ID inexistant → PassengerNotFoundException")
    void supprimerPassager_notFound() {
        UUID unknownId = UUID.randomUUID();
        when(passengerRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passengerService.supprimerPassager(unknownId))
                .isInstanceOf(PassengerNotFoundException.class);

        verify(passengerRepository, never()).delete(any());
        verify(eventProducer, never()).publishPassengerDeactivated(any());
    }
}