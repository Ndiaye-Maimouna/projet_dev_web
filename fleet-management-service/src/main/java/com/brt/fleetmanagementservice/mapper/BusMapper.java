package com.brt.fleetmanagementservice.mapper;

import com.brt.fleetmanagementservice.dto.request.BusRequestDTO;
import com.brt.fleetmanagementservice.dto.response.BusResponseDTO;
import com.brt.fleetmanagementservice.entity.Bus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BusMapper {

    public Bus toEntity(BusRequestDTO dto) {
        if (dto == null) return null;
        Bus bus = new Bus();
        bus.setImmatriculation(dto.getImmatriculation());
        bus.setCapacite(dto.getCapacite());
        bus.setMarque(dto.getMarque());
        bus.setModele(dto.getModele());
        bus.setDateAcquisition(dto.getDateAcquisition());
        bus.setKilometrage(dto.getKilometrage());
        bus.setStatut(dto.getStatut());
        return bus;
    }

    public BusResponseDTO toResponseDTO(Bus bus) {
        if (bus == null) return null;
        return BusResponseDTO.builder()
                .id(bus.getId())
                .immatriculation(bus.getImmatriculation())
                .capacite(bus.getCapacite())
                .marque(bus.getMarque())
                .modele(bus.getModele())
                .dateAcquisition(bus.getDateAcquisition())
                .kilometrage(bus.getKilometrage())
                .statut(bus.getStatut())
                .nombreAffectations(bus.getAffectations() != null
                        ? bus.getAffectations().size() : 0)
                .build();
    }

    public List<BusResponseDTO> toResponseDTOList(List<Bus> busList) {
        if (busList == null) return List.of();
        return busList.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public void updateEntityFromDTO(BusRequestDTO dto, Bus bus) {
        if (dto == null || bus == null) return;
        bus.setImmatriculation(dto.getImmatriculation());
        bus.setCapacite(dto.getCapacite());
        bus.setMarque(dto.getMarque());
        bus.setModele(dto.getModele());
        bus.setKilometrage(dto.getKilometrage());
        bus.setStatut(dto.getStatut());
    }
}