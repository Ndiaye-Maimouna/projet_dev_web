package com.brt.operationservice.repository;

import com.brt.operationservice.entity.Bus;
import com.brt.operationservice.entity.Trajet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrajetRepository extends JpaRepository<Trajet, UUID> {
    List<Trajet> findByStatut(String statut);
    List<Trajet> findByLigneId(UUID ligneId);
    List<Trajet> findByHeureDepartBetween(LocalDateTime debut, LocalDateTime fin);
    List<Trajet> findByStationDepartIdAndHeureDepartBetween(
            String stationId,
            LocalDateTime debut,
            LocalDateTime fin
    );
}
