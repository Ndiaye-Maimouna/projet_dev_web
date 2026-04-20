package com.brt.operationservice.controller;

import com.brt.operationservice.dto.request.BusRequestDto;
import com.brt.operationservice.dto.response.BusResponseDTO;
import com.brt.operationservice.entity.Bus;
import com.brt.operationservice.mapper.BusMapper;
import com.brt.operationservice.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bus")
@CrossOrigin(origins = "*")
public class BusController {

    @Autowired
    private BusService busService;
    private final BusMapper busMapper;

    @PostMapping
    public ResponseEntity<BusResponseDTO> ajouterBus(@RequestBody @Valid BusRequestDto dto) {
        try {
            Bus bus     = busMapper.toEntity(dto);
            Bus created = busService.ajouterBus(bus);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(busMapper.toResponseDTO(created));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body((BusResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<BusResponseDTO>> getTousLesBus() {
        return ResponseEntity.ok(
                busMapper.toResponseDTOList(busService.getTousLesBus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusResponseDTO> getBusById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(
                    busMapper.toResponseDTO(busService.getBusById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body((BusResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusResponseDTO> modifierBus(
            @PathVariable UUID id,
            @RequestBody @Valid BusRequestDto dto) {
        try {
            Bus bus     = busService.getBusById(id);
            busMapper.updateEntityFromDTO(dto, bus);
            Bus updated = busService.modifierBus(id, bus);
            return ResponseEntity.ok(busMapper.toResponseDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body((BusResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<BusResponseDTO>> getBusDisponibles() {

        List<Bus> busDisponibles = busService.getBusDisponibles();
        return ResponseEntity.ok(busMapper.toResponseDTOList(busDisponibles));
    }


    // ─── DISPONIBILITÉ ────────────────────────────────────────

    @GetMapping("/{id}/disponibilite")
    public ResponseEntity<?> verifierDisponibilite(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        try {
            boolean disponible = busService.verifierDisponibilite(id, debut, fin);
            return ResponseEntity.ok(Map.of(
                    "busId",       id,
                    "debut",       debut,
                    "fin",         fin,
                    "disponible",  disponible
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }



    // ─── AFFECTATION ─────────────────────────────────────────

    @PostMapping("/{busId}/affecter/{ligneId}")
    public ResponseEntity<?> affecterBusALigne(
            @PathVariable UUID busId,
            @PathVariable UUID ligneId) {
        try {
            Bus bus = busService.affecterBusALigne(busId, ligneId);
            return ResponseEntity.ok(Map.of(
                    "message", "Bus affecté à la ligne avec succès.",
                    "busId",   busId,
                    "ligneId", ligneId,
                    "statut",  bus.getStatut()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @DeleteMapping("/{busId}/desaffecter/{ligneId}")
    public ResponseEntity<?> desaffecterBus(
            @PathVariable UUID busId,
            @PathVariable UUID ligneId) {
        try {
            Bus bus = busService.desaffecterBus(busId, ligneId);
            return ResponseEntity.ok(Map.of(
                    "message", "Bus désaffecté avec succès.",
                    "busId",   busId,
                    "ligneId", ligneId,
                    "statut",  bus.getStatut()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }


    @GetMapping("/{id}/kilometrage")
    public ResponseEntity<?> getKilometrage(@PathVariable String id) {
        try {
            double km = busService.calculerKilometrageTotal(id);
            return ResponseEntity.ok(Map.of(
                    "busId",        id,
                    "kilometrageKm", Math.round(km * 100.0) / 100.0
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

}
