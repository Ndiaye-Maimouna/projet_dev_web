package com.brt.fleetmanagementservice.mapper;

import com.brt.fleetmanagementservice.dto.request.ConducteurRequestDTO;
import com.brt.fleetmanagementservice.dto.response.ConducteurResponseDTO;
import com.brt.fleetmanagementservice.entity.Conducteur;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConducteurMapper {

    public Conducteur toEntity(ConducteurRequestDTO dto) {
        if (dto == null) return null;
        Conducteur c = new Conducteur();
        c.setNom(dto.getNom());
        c.setPrenom(dto.getPrenom());
        c.setNumeroPermis(dto.getNumeroPermis());
        c.setTelephone(dto.getTelephone());
        c.setEmail(dto.getEmail());
        c.setDateEmbauche(dto.getDateEmbauche());
        c.setStatut(dto.getStatut());
        return c;
    }

    public ConducteurResponseDTO toResponseDTO(Conducteur c) {
        if (c == null) return null;
        return ConducteurResponseDTO.builder()
                .id(c.getId())
                .nom(c.getNom())
                .prenom(c.getPrenom())
                .numeroPermis(c.getNumeroPermis())
                .telephone(c.getTelephone())
                .email(c.getEmail())
                .dateEmbauche(c.getDateEmbauche())
                .statut(String.valueOf(c.getStatut()))
                .nombreAffectations(c.getAffectations() != null
                        ? c.getAffectations().size() : 0)
                .build();
    }

    public List<ConducteurResponseDTO> toResponseDTOList(List<Conducteur> list) {
        if (list == null) return List.of();
        return list.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public void updateEntityFromDTO(ConducteurRequestDTO dto, Conducteur c) {
        if (dto == null || c == null) return;
        c.setNom(dto.getNom());
        c.setPrenom(dto.getPrenom());
        c.setTelephone(dto.getTelephone());
        c.setEmail(dto.getEmail());
        c.setStatut(dto.getStatut());
    }
}