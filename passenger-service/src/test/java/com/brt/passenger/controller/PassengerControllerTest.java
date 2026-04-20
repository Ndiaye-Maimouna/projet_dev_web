package com.brt.passenger.controller;

import com.brt.passenger.dto.request.CreatePassagerRequest;
import com.brt.passenger.dto.response.PassengerResponse;
import com.brt.passenger.exception.DuplicatePassengerException;
import com.brt.passenger.exception.PassengerNotFoundException;
import com.brt.passenger.service.PassengerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static com.brt.passenger.domain.model.PassagerStatus.ACTIVE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PassengerController.class)
@DisplayName("PassengerController - Tests d'intégration (MockMvc)")
class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassengerService passengerService;

    private static final UUID SAMPLE_ID = UUID.fromString("a0000000-0000-0000-0000-000000000001");

    private PassengerResponse buildSampleResponse() {
        return PassengerResponse.builder()
                .id(SAMPLE_ID)
                .firstName("Fatou")
                .lastName("Diallo")
                .fullName("Fatou Diallo")
                .email("fatou.diallo@brt.sn")
                .phoneNumber("+221771234567")
                .status(ACTIVE)
                .hasActiveSubscription(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    // ── POST /api/v1/passengers ────────────────────────────────────────

    @Test
    @DisplayName("POST /passengers → 201 Created avec corps valide")
    void createPassenger_returns201() throws Exception {
        // CreatePassagerRequest utilise nom/prenom/email/telephone
        CreatePassagerRequest req = new CreatePassagerRequest();
        req.setPrenom("Fatou");
        req.setNom("Diallo");
        req.setEmail("fatou.diallo@brt.sn");
        req.setTelephone("+221771234567");

        when(passengerService.creerPassager(any())).thenReturn(buildSampleResponse());

        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("fatou.diallo@brt.sn"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /passengers sans email → 400 Bad Request")
    void createPassenger_missingEmail_returns400() throws Exception {
        CreatePassagerRequest req = new CreatePassagerRequest();
        req.setPrenom("Fatou");
        req.setNom("Diallo");
        // email manquant
        req.setTelephone("+221771234567");

        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /passengers avec email dupliqué → 409 Conflict")
    void createPassenger_duplicateEmail_returns409() throws Exception {
        CreatePassagerRequest req = new CreatePassagerRequest();
        req.setPrenom("Fatou");
        req.setNom("Diallo");
        req.setEmail("fatou.diallo@brt.sn");
        req.setTelephone("+221771234567");

        when(passengerService.creerPassager(any()))
                .thenThrow(new DuplicatePassengerException("email", "fatou.diallo@brt.sn"));

        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /passengers avec téléphone dupliqué → 409 Conflict")
    void createPassenger_duplicateTelephone_returns409() throws Exception {
        CreatePassagerRequest req = new CreatePassagerRequest();
        req.setPrenom("Mamadou");
        req.setNom("Ndiaye");
        req.setEmail("mamadou@brt.sn");
        req.setTelephone("+221771234567");

        when(passengerService.creerPassager(any()))
                .thenThrow(new DuplicatePassengerException("téléphone", "+221771234567"));

        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ── GET /api/v1/passengers/{id} ────────────────────────────────────

    @Test
    @DisplayName("GET /passengers/{id} → 200 OK avec passager existant")
    void getPassenger_found_returns200() throws Exception {
        when(passengerService.getPassagerById(SAMPLE_ID)).thenReturn(buildSampleResponse());

        mockMvc.perform(get("/api/v1/passengers/{id}", SAMPLE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(SAMPLE_ID.toString()))
                .andExpect(jsonPath("$.data.fullName").value("Fatou Diallo"));
    }

    @Test
    @DisplayName("GET /passengers/{id} → 404 Not Found pour ID inexistant")
    void getPassenger_notFound_returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(passengerService.getPassagerById(unknownId))
                .thenThrow(new PassengerNotFoundException(unknownId));

        mockMvc.perform(get("/api/v1/passengers/{id}", unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ── DELETE /api/v1/passengers/{id} ────────────────────────────────

    @Test
    @DisplayName("DELETE /passengers/{id} → 204 No Content pour passager existant")
    void deletePassenger_returns204() throws Exception {
        // supprimerPassager ne lève pas d'exception → 204 attendu
        mockMvc.perform(delete("/api/v1/passengers/{id}", SAMPLE_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /passengers/{id} → 404 Not Found pour ID inexistant")
    void deletePassenger_notFound_returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        org.mockito.Mockito.doThrow(new PassengerNotFoundException(unknownId))
                .when(passengerService).supprimerPassager(unknownId);

        mockMvc.perform(delete("/api/v1/passengers/{id}", unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ── GET /api/v1/passengers/health ─────────────────────────────────

    @Test
    @DisplayName("GET /passengers/health → 200 OK")
    void health_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/passengers/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}