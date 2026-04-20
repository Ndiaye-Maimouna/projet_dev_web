package com.brt.operationservice.mapper;

import com.brt.operationservice.dto.request.TrajetRequestDTO;
import com.brt.operationservice.dto.response.TrajetResponseDTO;
import com.brt.operationservice.entity.*;
import com.brt.operationservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrajetMapper {

    private final LigneBRTRepository  ligneRepo;
    private final StationRepository   stationRepo;
    private final BusRepository       busRepo;

    // ── Request → Entity ───────────────────────────────────
    public Trajet toEntity(TrajetRequestDTO dto) {
        if (dto == null) return null;

        // Résolution des entités associées via leurs IDs
        LigneBRT ligne = ligneRepo.findById(dto.getLigneId())
                .orElseThrow(() -> new RuntimeException(
                        "Ligne introuvable : " + dto.getLigneId()));

        Station stationDepart = stationRepo.findById(dto.getStationDepartId())
                .orElseThrow(() -> new RuntimeException(
                        "Station de départ introuvable : " + dto.getStationDepartId()));

        Station stationArrivee = stationRepo.findById(dto.getStationArriveeId())
                .orElseThrow(() -> new RuntimeException(
                        "Station d'arrivée introuvable : " + dto.getStationArriveeId()));

        Bus bus = busRepo.findById(dto.getBusId())
                .orElseThrow(() -> new RuntimeException(
                        "Bus introuvable : " + dto.getBusId()));

        // Validation métier : arrivée après départ
        if (!dto.getHeureArrivee().isAfter(dto.getHeureDepart())) {
            throw new RuntimeException(
                    "L'heure d'arrivée doit être postérieure à l'heure de départ.");
        }

        // Validation : station départ ≠ station arrivée
        if (dto.getStationDepartId().equals(dto.getStationArriveeId())) {
            throw new RuntimeException(
                    "La station de départ et d'arrivée ne peuvent pas être identiques.");
        }

        Trajet trajet = new Trajet();
        trajet.setHeureDepart(dto.getHeureDepart());
        trajet.setHeureArrivee(dto.getHeureArrivee());
        trajet.setStatut(dto.getStatut());
        trajet.setLigne(ligne);
        trajet.setStationDepart(stationDepart);
        trajet.setStationArrivee(stationArrivee);
        trajet.setBus(bus);
        return trajet;
    }

    // ── Entity → Response ──────────────────────────────────
    public TrajetResponseDTO toResponseDTO(Trajet trajet) {
        if (trajet == null) return null;

        // Calcul durée en minutes
        long dureeMinutes = 0;
        if (trajet.getHeureDepart() != null && trajet.getHeureArrivee() != null) {
            dureeMinutes = Duration.between(
                    trajet.getHeureDepart(),
                    trajet.getHeureArrivee()
            ).toMinutes();
        }

        return TrajetResponseDTO.builder()
                .id(trajet.getId())
                .heureDepart(trajet.getHeureDepart())
                .heureArrivee(trajet.getHeureArrivee())
                .statut(trajet.getStatut())
                .dureeMinutes(dureeMinutes)
                // Ligne
                .ligneId(trajet.getLigne() != null
                        ? trajet.getLigne().getId() : null)
                .ligneNom(trajet.getLigne() != null
                        ? trajet.getLigne().getNom() : null)
                // Station départ
                .stationDepartId(trajet.getStationDepart() != null
                        ? trajet.getStationDepart().getId() : null)
                .stationDepartNom(trajet.getStationDepart() != null
                        ? trajet.getStationDepart().getNom() : null)
                // Station arrivée
                .stationArriveeId(trajet.getStationArrivee() != null
                        ? trajet.getStationArrivee().getId() : null)
                .stationArriveeNom(trajet.getStationArrivee() != null
                        ? trajet.getStationArrivee().getNom() : null)
                // Bus
                .busId(trajet.getBus() != null
                        ? trajet.getBus().getId() : null)
                .busImmatriculation(trajet.getBus() != null
                        ? trajet.getBus().getImmatriculation() : null)
                .build();
    }

    // ── Liste Entity → Liste Response ──────────────────────
    public List<TrajetResponseDTO> toResponseDTOList(List<Trajet> trajets) {
        if (trajets == null) return List.of();
        return trajets.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── Mise à jour partielle ───────────────────────────────
    public void updateEntityFromDTO(TrajetRequestDTO dto, Trajet trajet) {
        if (dto == null || trajet == null) return;
        trajet.setHeureDepart(dto.getHeureDepart());
        trajet.setHeureArrivee(dto.getHeureArrivee());
        trajet.setStatut(dto.getStatut());

        if (dto.getLigneId() != null) {
            LigneBRT ligne = ligneRepo.findById(dto.getLigneId())
                    .orElseThrow(() -> new RuntimeException("Ligne introuvable"));
            trajet.setLigne(ligne);
        }
        if (dto.getBusId() != null) {
            Bus bus = busRepo.findById(dto.getBusId())
                    .orElseThrow(() -> new RuntimeException("Bus introuvable"));
            trajet.setBus(bus);
        }
    }
}