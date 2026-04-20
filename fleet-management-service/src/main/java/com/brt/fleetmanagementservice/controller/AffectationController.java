package com.brt.fleetmanagementservice.controller;

import com.brt.fleetmanagementservice.dto.request.AffectationRequestDTO;
import com.brt.fleetmanagementservice.dto.response.AffectationResponseDTO;
import com.brt.fleetmanagementservice.entity.Affectation;
import com.brt.fleetmanagementservice.mapper.AffectationMapper;
import com.brt.fleetmanagementservice.service.AffectationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/fleet/affectations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AffectationController {

    private final AffectationService affectationService;
    private final AffectationMapper affectationMapper;


    @PostMapping
    public ResponseEntity<AffectationResponseDTO> creerAffectation(
            @RequestBody @Valid AffectationRequestDTO dto) {
        try {
            Affectation affectation = affectationMapper.toEntity(dto);
            Affectation saved       = affectationService.creerAffectation(affectation);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(affectationMapper.toResponseDTO(saved));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body((AffectationResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }


    @PutMapping("/{id}/annuler")
    public ResponseEntity<?> annulerAffectation(@PathVariable UUID id) {
        try {
            Affectation annulee = affectationService.annulerAffectation(id);
            return ResponseEntity.ok(Map.of(
                    "message",      "Affectation annulée avec succès.",
                    "affectation",  affectationMapper.toResponseDTO(annulee)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }


    @GetMapping
    public ResponseEntity<List<AffectationResponseDTO>> getToutesAffectations() {
        return ResponseEntity.ok(
                affectationMapper.toResponseDTOList(
                        affectationService.getToutesAffectations()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<AffectationResponseDTO> getAffectationById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(
                    affectationMapper.toResponseDTO(
                            affectationService.getAffectationById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body((AffectationResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }


    @GetMapping("/ligne/{ligneId}")
    public ResponseEntity<List<AffectationResponseDTO>> getAffectationsByLigne(
            @PathVariable UUID ligneId) {
        return ResponseEntity.ok(
                affectationMapper.toResponseDTOList(
                        affectationService.getAffectationsByLigne(ligneId)));
    }
}
