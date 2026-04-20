package com.brt.operationservice.mapper;

import com.brt.operationservice.dto.request.StationRequestDTO;
import com.brt.operationservice.dto.response.StationResponseDTO;
import com.brt.operationservice.entity.Station;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StationMapper {

    // ── Request → Entity ───────────────────────────────────
    public Station toEntity(StationRequestDTO dto) {
        if (dto == null) return null;

        Station station = new Station();
        station.setNom(dto.getNom());
        station.setLatitude(dto.getLatitude());
        station.setLongitude(dto.getLongitude());
        station.setActif(dto.isActif());
        // La ligne est injectée dans le service via ligneId
        return station;
    }

    // ── Entity → Response ──────────────────────────────────
    public StationResponseDTO toResponseDTO(Station station) {
        if (station == null) return null;

        return StationResponseDTO.builder()
                .id(station.getId())
                .nom(station.getNom())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .actif(station.isActif())
                .ligneId(station.getLigne() != null
                        ? station.getLigne().getId() : null)
                .ligneNom(station.getLigne() != null
                        ? station.getLigne().getNom() : null)
                .build();
    }

    // ── Liste Entity → Liste Response ──────────────────────
    public List<StationResponseDTO> toResponseDTOList(List<Station> stations) {
        if (stations == null) return List.of();
        return stations.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── Mise à jour partielle ───────────────────────────────
    public void updateEntityFromDTO(StationRequestDTO dto, Station station) {
        if (dto == null || station == null) return;
        station.setNom(dto.getNom());
        station.setLatitude(dto.getLatitude());
        station.setLongitude(dto.getLongitude());
        station.setActif(dto.isActif());
    }
}