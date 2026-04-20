package com.brt.fleetmanagementservice.mapper;

import com.brt.fleetmanagementservice.dto.request.AffectationRequestDTO;
import com.brt.fleetmanagementservice.dto.response.AffectationResponseDTO;
import com.brt.fleetmanagementservice.entity.Affectation;
import com.brt.fleetmanagementservice.entity.Bus;
import com.brt.fleetmanagementservice.entity.Conducteur;
import com.brt.fleetmanagementservice.repository.BusRepository;
import com.brt.fleetmanagementservice.repository.ConducteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AffectationMapper {

    private final ConducteurRepository conducteurRepo;
    private final BusRepository busRepo;

    public Affectation toEntity(AffectationRequestDTO dto) {
        if (dto == null) return null;

        Conducteur conducteur = conducteurRepo.findById(dto.getConducteurId())
                .orElseThrow(() -> new RuntimeException(
                        "Conducteur introuvable : " + dto.getConducteurId()));

        Bus bus = busRepo.findById(dto.getBusId())
                .orElseThrow(() -> new RuntimeException(
                        "Bus introuvable : " + dto.getBusId()));

        if (!dto.getDateFin().isAfter(dto.getDateDebut())) {
            throw new RuntimeException("La date de fin doit être après la date de début.");
        }

        Affectation a = new Affectation();
        a.setConducteur(conducteur);
        a.setBus(bus);
        a.setLigneId(dto.getLigneId());
        a.setDateDebut(dto.getDateDebut());
        a.setDateFin(dto.getDateFin());
        a.setStatut(dto.getStatut());
        return a;
    }

    public AffectationResponseDTO toResponseDTO(Affectation a) {
        if (a == null) return null;

        long dureeMinutes = 0;
        if (a.getDateDebut() != null && a.getDateFin() != null) {
            dureeMinutes = Duration.between(a.getDateDebut(), a.getDateFin()).toMinutes();
        }

        return AffectationResponseDTO.builder()
                .id(a.getId())
                .dateDebut(a.getDateDebut())
                .dateFin(a.getDateFin())
                .dureeMinutes(dureeMinutes)
                .statut(a.getStatut())
                .ligneId(a.getLigneId())
                .conducteurId(a.getConducteur() != null
                        ? a.getConducteur().getId() : null)
                .conducteurNom(a.getConducteur() != null
                        ? a.getConducteur().getNom() : null)
                .conducteurPrenom(a.getConducteur() != null
                        ? a.getConducteur().getPrenom() : null)
                .busId(a.getBus() != null ? a.getBus().getId() : null)
                .busImmatriculation(a.getBus() != null
                        ? a.getBus().getImmatriculation() : null)
                .build();
    }

    public List<AffectationResponseDTO> toResponseDTOList(List<Affectation> list) {
        if (list == null) return List.of();
        return list.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}