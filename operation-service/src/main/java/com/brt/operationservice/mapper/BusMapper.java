package com.brt.operationservice.mapper;

import com.brt.operationservice.dto.request.BusRequestDto;
import com.brt.operationservice.dto.response.BusResponseDTO;
import com.brt.operationservice.entity.Bus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BusMapper {

    // ── Request → Entity ───────────────────────────────────
    public Bus toEntity(BusRequestDto dto) {
        if (dto == null) return null;

        Bus bus = new Bus();
        bus.setImmatriculation(dto.getImmatriculation());
        bus.setCapacite(dto.getCapacite());
        bus.setStatut(dto.getStatut());
        return bus;
    }

    // ── Entity → Response ──────────────────────────────────
    public BusResponseDTO toResponseDTO(Bus bus) {
        if (bus == null) return null;

        return BusResponseDTO.builder()
                .id(bus.getId())
                .immatriculation(bus.getImmatriculation())
                .capacite(bus.getCapacite())
                .statut(bus.getStatut())
                .build();
    }

    // ── Liste Entity → Liste Response ──────────────────────
    public List<BusResponseDTO> toResponseDTOList(List<Bus> busList) {
        if (busList == null) return List.of();
        return busList.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── Mise à jour partielle Entity depuis Request ─────────
    public void updateEntityFromDTO(BusRequestDto dto, Bus bus) {
        if (dto == null || bus == null) return;
        bus.setImmatriculation(dto.getImmatriculation());
        bus.setCapacite(dto.getCapacite());
        bus.setStatut(dto.getStatut());
    }
}