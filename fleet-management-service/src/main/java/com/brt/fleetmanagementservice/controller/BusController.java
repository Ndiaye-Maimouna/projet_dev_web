package com.brt.fleetmanagementservice.controller;

import com.brt.fleetmanagementservice.dto.response.BusResponseDTO;
import com.brt.fleetmanagementservice.mapper.BusMapper;
import com.brt.fleetmanagementservice.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/fleet/buses")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;
    private final BusMapper busMapper;


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


}