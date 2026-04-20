package com.brt.passenger.controller;

import com.brt.passenger.dto.request.CreatePassagerRequest;
import com.brt.passenger.dto.request.UpdatePassengerRequest;
import com.brt.passenger.dto.response.ApiResponse;
import com.brt.passenger.dto.response.PassengerResponse;
import com.brt.passenger.dto.response.TripHistoryResponse;
import com.brt.passenger.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Endpoints du passenger-service — conformes à la spec BRT.
 *
 * ↑ Expose :
 *   POST   /passengers              → créer un compte passager
 *   GET    /passengers/{id}         → profil d'un passager
 *   PUT    /passengers/{id}         → mettre à jour le profil
 *   DELETE /passengers/{id}         → supprimer un compte
 *   GET    /passengers/{id}/historique  → historique des trajets
 *   GET    /passengers/{id}/abonnements → abonnements actifs
 *
 * ↓ Consomme (via WebClient, géré dans PassengerService) :
 *   POST /tickets                   → ticketing-payment-service (Maimouna)
 *   GET  /tickets?passengerId={id}  → ticketing-payment-service (Maimouna)
 *   GET  /stations                  → operation-service (Syaka)
 *   GET  /lines/{id}/next-bus       → operation-service (Syaka)
 */
@Slf4j
@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    // ── POST /api/passengers ───────────────────────────────────────────
    /**
     * Spec : POST /passengers — Créer un compte passager
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PassengerResponse>> creerPassager(
            @Valid @RequestBody CreatePassagerRequest request) {

        log.info("Création passager : {} {}", request.getPrenom(), request.getNom());
        PassengerResponse response = passengerService.creerPassager(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Passager créé avec succès", response));
    }

    // ── GET /api/passengers/{id} ───────────────────────────────────────
    /**
     * Spec : GET /passengers/{id} — Infos d'un passager (nom, email, tél)
     */
    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponse> getPassager(
            @PathVariable UUID id) {

        PassengerResponse response = passengerService.getPassagerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<PassengerResponse> getByUserId(@PathVariable UUID userId) {
        PassengerResponse response = passengerService.getByUserId(userId);
        return ResponseEntity.ok(response);
    }

    // ── PUT /api/passengers/{id} ───────────────────────────────────────
    /**
     * Spec : PUT /passengers/{id} — Mettre à jour le profil passager
     * (PUT complet, pas PATCH partiel)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PassengerResponse>> updatePassager(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePassengerRequest request) {

        PassengerResponse response = passengerService.updatePassager(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Profil mis à jour", response));
    }

    // ── DELETE /api/passengers/{id} ───────────────────────────────────
    /**
     * Spec : DELETE /passengers/{id} — Supprimer un compte passager
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimerPassager(
            @PathVariable UUID id) {

        passengerService.supprimerPassager(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Compte passager supprimé")
                .build());
    }

    // ── GET /api/passengers/{id}/historique ───────────────────────────
    /**
     * Spec : GET /passengers/{id}/historique — Historique des trajets
     * Consomme GET /tickets?passengerId={id} → ticketing-service (Maimouna)
     */
    @GetMapping("/{id}/historique")
    public ResponseEntity<ApiResponse<Page<TripHistoryResponse>>> getHistorique(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        Page<TripHistoryResponse> result = passengerService.getHistorique(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ── GET /api/passengers/{id}/abonnements ──────────────────────────
    /**
     * Spec : GET /passengers/{id}/abonnements — Abonnements actifs
     */
    @GetMapping("/{id}/abonnements")
    public ResponseEntity<ApiResponse<?>> getAbonnements(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.ok(passengerService.getAbonnements(id)));
    }
}
