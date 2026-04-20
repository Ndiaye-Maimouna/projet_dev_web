package com.brt.operationservice.controller;

import com.brt.operationservice.dto.request.LigneBRTRequestDto;
import com.brt.operationservice.dto.response.LigneBRTResponseDTO;
import com.brt.operationservice.entity.Bus;
import com.brt.operationservice.entity.LigneBRT;
import com.brt.operationservice.mapper.LigneBRTMapper;
import com.brt.operationservice.repository.LigneBRTRepository;
import com.brt.operationservice.service.LigneBRTService;
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
@RequestMapping("/api/lignes")
@CrossOrigin(origins = "*")
public class LigneBRTController {

    @Autowired private LigneBRTService ligneBRTService;
    private final LigneBRTMapper ligneBRTMapper;


    @PostMapping
    public ResponseEntity<LigneBRTResponseDTO> creerLigne(@RequestBody LigneBRTRequestDto ligne) {
        try {
            LigneBRT ligneBRT = ligneBRTMapper.toEntity(ligne);
            LigneBRT created = ligneBRTService.creerLigne(ligneBRT);
            return ResponseEntity.status(HttpStatus.CREATED).body(ligneBRTMapper.toResponseDTO(created));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body((LigneBRTResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<LigneBRTResponseDTO>> getToutesLignes() {
        List<LigneBRT> Lignes = ligneBRTService.getToutesLignes();
        return ResponseEntity.ok(ligneBRTMapper.toResponseDTOList(Lignes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LigneBRTResponseDTO> getLigneById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(
                    ligneBRTMapper.toResponseDTO(ligneBRTService.getLigneById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body((LigneBRTResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<LigneBRTResponseDTO> modifierLigne(
            @PathVariable UUID id,
            @RequestBody LigneBRTRequestDto dto) {
        try {

            LigneBRT ligne = ligneBRTService.getLigneById(id);
            ligneBRTMapper.updateEntityFromDTO(dto, ligne);
            LigneBRT updated = ligneBRTService.modifierLigne(id, ligne);
            return ResponseEntity.ok(ligneBRTMapper.toResponseDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body((LigneBRTResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerLigne(@PathVariable UUID id) {
        try {
            ligneBRTService.supprimerLigne(id);
            return ResponseEntity.ok(Map.of("message", "Ligne supprimée avec succès."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }


    @PostMapping("/{ligneId}/stations/{stationId}")
    public ResponseEntity<?> ajouterStation(
            @PathVariable UUID ligneId,
            @PathVariable UUID stationId) {
        try {
            LigneBRT updated = ligneBRTService.ajouterStationALigne(ligneId, stationId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @DeleteMapping("/{ligneId}/stations/{stationId}")
    public ResponseEntity<LigneBRTResponseDTO> retirerStation(
            @PathVariable UUID ligneId,
            @PathVariable String stationId) {
        try {
            LigneBRT updated = ligneBRTService.retirerStationDeLigne(ligneId, stationId);
            return ResponseEntity.ok(ligneBRTMapper.toResponseDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body((LigneBRTResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }


    @GetMapping("/{id}/duree")
    public ResponseEntity<?> calculerDuree(@PathVariable UUID id) {
        try {
            double duree = ligneBRTService.calculerDureeLigne(id);
            return ResponseEntity.ok(Map.of(
                    "ligneId", id,
                    "dureeEstimeeMinutes", duree
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping("/{id}/bus-necessaires")
    public ResponseEntity<?> calculerBusNecessaires(@PathVariable UUID id) {
        try {
            int nbBus = ligneBRTService.calculerNombreBusNecessaires(id);
            return ResponseEntity.ok(Map.of(
                    "ligneId", id,
                    "nombreBusNecessaires", nbBus
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping("/{id}/operationnelle")
    public ResponseEntity<?> verifierOperationnelle(@PathVariable UUID id) {
        try {
            boolean operationnelle = ligneBRTService.estOperationnelle(id);
            return ResponseEntity.ok(Map.of(
                    "ligneId", id,
                    "estOperationnelle", operationnelle
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @GetMapping("/{id}/statistiques")
    public ResponseEntity<?> getStatistiques(@PathVariable UUID id) {
        try {
            Map<String, Object> stats = ligneBRTService.getStatistiquesLigne(id);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }
}
