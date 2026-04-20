package com.brt.operationservice.repository;

import com.brt.operationservice.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StationRepository extends JpaRepository<Station, UUID> {
    List<Station> findByActifTrue();
    List<Station> findByLigneId(UUID ligneId);
    Optional<Station> findByNomAndLigneId(String nom, UUID ligneId);
}