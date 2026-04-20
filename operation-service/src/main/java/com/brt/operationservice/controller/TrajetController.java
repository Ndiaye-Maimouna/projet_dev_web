package com.brt.operationservice.controller;

import com.brt.operationservice.dto.request.TrajetRequestDTO;
import com.brt.operationservice.dto.response.TrajetResponseDTO;
import com.brt.operationservice.entity.Trajet;
import com.brt.operationservice.mapper.TrajetMapper;
import com.brt.operationservice.service.StationService;
import com.brt.operationservice.service.TrajetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trajets")
@CrossOrigin(origins = "*")
public class TrajetController {

    @Autowired
    private TrajetService trajetService;
    private final TrajetMapper trajetMapper;

    // ─── CRUD ───────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<TrajetResponseDTO> planifierTrajet(@RequestBody @Valid TrajetRequestDTO trajet) {
        try {
            Trajet newTrajet = trajetMapper.toEntity(trajet);
            Trajet created = trajetService.planifierTrajet(newTrajet);
            return ResponseEntity.status(HttpStatus.CREATED).body(trajetMapper.toResponseDTO(created));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body((TrajetResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<TrajetResponseDTO>> getTousLesTrajets() {
        List<Trajet> trajets = trajetService.getTousLesTrajets();
        return ResponseEntity.ok(trajetMapper.toResponseDTOList(trajets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrajetResponseDTO> getTrajetById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(
                    trajetMapper.toResponseDTO(trajetService.getTrajetById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body((TrajetResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping("/ligne/{ligneId}")
    public ResponseEntity<List<TrajetResponseDTO>> getTrajetsByLigne(@PathVariable UUID ligneId) {
        return ResponseEntity.ok(
                trajetMapper.toResponseDTOList(trajetService.getTrajetsByLigne(ligneId)));
    }

    @GetMapping("/plage")
    public ResponseEntity<?> getTrajetsByPlage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        try {
            List<Trajet> trajets = trajetService.getTrajetsByPlage(debut, fin);
            return ResponseEntity.ok(Map.of(
                    "debut",   debut,
                    "fin",     fin,
                    "trajets", trajets,
                    "total",   trajets.size()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    // ─── GESTION DU STATUT ───────────────────────────────────

    @PatchMapping("/{id}/statut")
    public ResponseEntity<?> mettreAJourStatut(
            @PathVariable UUID id,
            @RequestParam String statut) {
        try {
            Trajet updated = trajetService.mettreAJourStatut(id, statut.toUpperCase());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    // ─── CALCULS & STATISTIQUES ──────────────────────────────

    @GetMapping("/{id}/duree")
    public ResponseEntity<?> calculerDuree(@PathVariable UUID id) {
        try {
            Duration duree = trajetService.calculerDuree(id);
            return ResponseEntity.ok(Map.of(
                    "trajetId",       id,
                    "dureeMinutes",   duree.toMinutes(),
                    "dureeHeures",    duree.toHours(),
                    "dureeFormatee",  String.format("%dh %02dmin",
                            duree.toHours(),
                            duree.toMinutesPart())
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping("/{id}/retard")
    public ResponseEntity<?> calculerRetard(@PathVariable UUID id) {
        try {
            long retard = trajetService.calculerRetard(id);
            return ResponseEntity.ok(Map.of(
                    "trajetId",       id,
                    "retardMinutes",  retard,
                    "aLHeure",        retard == 0
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }



}
