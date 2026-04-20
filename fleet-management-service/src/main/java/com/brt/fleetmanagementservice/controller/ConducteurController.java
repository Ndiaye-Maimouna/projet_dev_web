package com.brt.fleetmanagementservice.controller;

import com.brt.fleetmanagementservice.dto.response.ConducteurResponseDTO;
import com.brt.fleetmanagementservice.entity.Conducteur;
import com.brt.fleetmanagementservice.mapper.ConducteurMapper;
import com.brt.fleetmanagementservice.service.ConducteurService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/fleet/conducteurs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ConducteurController {

    private final ConducteurService conducteurService;
    private final ConducteurMapper conducteurMapper;


    @GetMapping("/{id}")
    public ResponseEntity<ConducteurResponseDTO> getConducteurById(@PathVariable UUID id) {
        try {
            Map<String, Object> resultat =
                    conducteurService.getConducteurAvecDisponibilite(id);
            Conducteur conducteur = (Conducteur) resultat.get("conducteur");
            resultat.put("conducteur", conducteurMapper.toResponseDTO(conducteur));

            return ResponseEntity.ok((ConducteurResponseDTO) resultat);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body((ConducteurResponseDTO) Map.of("erreur", e.getMessage()));
        }
    }


    @GetMapping
    public ResponseEntity<List<ConducteurResponseDTO>> getTousLesConducteurs() {
        return ResponseEntity.ok(
                conducteurMapper.toResponseDTOList(
                        conducteurService.getTousConducteurs()));
    }


    @GetMapping("/disponibles")
    public ResponseEntity<?> getConducteursDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin) {
        try {
            List<ConducteurResponseDTO> list = conducteurMapper
                    .toResponseDTOList(
                            conducteurService.getConducteursDisponibles(debut, fin));
            return ResponseEntity.ok(Map.of(
                    "debut",        debut,
                    "fin",          fin,
                    "conducteurs",  list,
                    "total",        list.size()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }
}