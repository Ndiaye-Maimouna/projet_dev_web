package com.brt.operationservice.mapper;

import com.brt.operationservice.dto.request.LigneBRTRequestDto;
import com.brt.operationservice.dto.response.LigneBRTResponseDTO;
import com.brt.operationservice.entity.LigneBRT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LigneBRTMapper {

    // Injectés pour mapper les sous-entités
    private final StationMapper stationMapper;
    private final BusMapper     busMapper;

    // ── Request → Entity ───────────────────────────────────
    public LigneBRT toEntity(LigneBRTRequestDto dto) {
        if (dto == null) return null;

        LigneBRT ligne = new LigneBRT();
        ligne.setNom(dto.getNom());
        ligne.setTerminusDepart(dto.getTerminusDepart());
        ligne.setTerminusArrivee(dto.getTerminusArrivee());
        ligne.setFrequence_minutes(dto.getFrequenceMinutes());
        return ligne;
    }

    // ── Entity → Response (avec détails complets) ──────────
    public LigneBRTResponseDTO toResponseDTO(LigneBRT ligne) {
        if (ligne == null) return null;

        return LigneBRTResponseDTO.builder()
                .id(ligne.getId())
                .nom(ligne.getNom())
                .terminusDepart(ligne.getTerminusDepart())
                .terminusArrivee(ligne.getTerminusArrivee())
                .frequenceMinutes(ligne.getFrequence_minutes())
                .nombreStations(ligne.getStations() != null
                        ? ligne.getStations().size() : 0)
                .nombreBus(ligne.getBus() != null
                        ? ligne.getBus().size() : 0)
                .stations(ligne.getStations() != null
                        ? stationMapper.toResponseDTOList(ligne.getStations())
                        : List.of())
                .bus(ligne.getBus() != null
                        ? busMapper.toResponseDTOList(ligne.getBus())
                        : List.of())
                .build();
    }

    // ── Liste Entity → Liste Response ──────────────────────
    public List<LigneBRTResponseDTO> toResponseDTOList(List<LigneBRT> lignes) {
        if (lignes == null) return List.of();
        return lignes.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── Mise à jour partielle ───────────────────────────────
    public void updateEntityFromDTO(LigneBRTRequestDto dto, LigneBRT ligne) {
        if (dto == null || ligne == null) return;
        ligne.setNom(dto.getNom());
        ligne.setTerminusDepart(dto.getTerminusDepart());
        ligne.setTerminusArrivee(dto.getTerminusArrivee());
        ligne.setFrequence_minutes(dto.getFrequenceMinutes());
    }
}