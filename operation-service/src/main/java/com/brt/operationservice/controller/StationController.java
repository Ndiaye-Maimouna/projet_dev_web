package com.brt.operationservice.controller;

import com.brt.operationservice.dto.request.StationRequestDTO;
import com.brt.operationservice.dto.response.StationResponseDTO;
import com.brt.operationservice.entity.Bus;
import com.brt.operationservice.entity.Station;
import com.brt.operationservice.entity.Trajet;
import com.brt.operationservice.mapper.StationMapper;
import com.brt.operationservice.service.StationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stations")
@CrossOrigin(origins = "*")
public class StationController {

    @Autowired
    private StationService stationService;
    private final StationMapper stationMapper;

    // ─── CRUD ───────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<StationResponseDTO> creerStation(@RequestBody @Valid StationRequestDTO station) {
        try {
            Station created = stationService.creerStation(station);
            StationResponseDTO response = stationMapper.toResponseDTO(created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body((StationResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<StationResponseDTO>> getToutesStations() {
        List<Station> stations = stationService.getToutesStations();
        return ResponseEntity.ok(stationMapper.toResponseDTOList(stations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStationById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(
                    stationMapper.toResponseDTO(stationService.getStationById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping("/actives")
    public ResponseEntity<List<StationResponseDTO>> getStationsActives() {
        return ResponseEntity.ok(
                stationMapper.toResponseDTOList(stationService.getStationsActives()));
    }

    @GetMapping("/ligne/{ligneId}")
    public ResponseEntity<List<StationResponseDTO>> getStationsByLigne(@PathVariable UUID ligneId) {
        return ResponseEntity.ok(
                stationMapper.toResponseDTOList(stationService.getStationsByLigne(ligneId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StationResponseDTO> modifierStation(
            @PathVariable UUID id,
            @RequestBody StationRequestDTO dto) {
        try {
            Station station = stationService.getStationById(id);
            stationMapper.updateEntityFromDTO(dto, station);
            Station updated = stationService.modifierStation(id, station);
            return ResponseEntity.ok(stationMapper.toResponseDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body((StationResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<?> desactiverStation(@PathVariable UUID id) {
        try {
            stationService.desactiverStation(id);
            return ResponseEntity.ok(Map.of("message", "Station désactivée avec succès."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<?> activerStation(@PathVariable UUID id) {
        try {
            stationService.activerStation(id);
            return ResponseEntity.ok(Map.of("message", "Station activée avec succès."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    // ─── CALCULS & MÉTRIQUES ─────────────────────────────────

    @GetMapping("/distance")
    public ResponseEntity<?> calculerDistance(
            @RequestParam UUID stationAId,
            @RequestParam UUID stationBId) {
        try {
            double distance = stationService.calculerDistanceEntre(stationAId, stationBId);
            return ResponseEntity.ok(Map.of(
                    "stationAId",   stationAId,
                    "stationBId",   stationBId,
                    "distanceKm",   Math.round(distance * 100.0) / 100.0
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping("/temps-trajet")
    public ResponseEntity<?> estimerTempsTrajet(
            @RequestParam UUID stationAId,
            @RequestParam UUID stationBId) {
        try {
            double temps = stationService.estimerTempsTrajet(stationAId, stationBId);
            return ResponseEntity.ok(Map.of(
                    "stationAId",       stationAId,
                    "stationBId",       stationBId,
                    "tempsEstimeMin",   Math.round(temps * 100.0) / 100.0
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping("/{id}/prochains-bus")
    public ResponseEntity<?> getProchainsBus(@PathVariable String id) {
        try {
            List<Trajet> trajets = stationService.getProchainsBus(id);
            return ResponseEntity.ok(Map.of(
                    "stationId",     id,
                    "prochainsbus",  trajets,
                    "total",         trajets.size()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping("/proches")
    public ResponseEntity<?> getStationsProches(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        try {
            List<Station> stations = stationService.getStationsProches(latitude, longitude);
            return ResponseEntity.ok(Map.of(
                    "latitude",  latitude,
                    "longitude", longitude,
                    "stations",  stations,
                    "total",     stations.size()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }
}