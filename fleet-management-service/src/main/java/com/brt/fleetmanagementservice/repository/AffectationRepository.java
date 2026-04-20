package com.brt.fleetmanagementservice.repository;

import com.brt.fleetmanagementservice.entity.Affectation;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AffectationRepository extends JpaRepository<Affectation, UUID> {
    List<Affectation> findByStatut(String statut);
    List<Affectation> findByBusId(UUID busId);
    List<Affectation> findByConducteurId(UUID conducteurId);
    List<Affectation> findByLigneId(UUID ligneId);

    // Vérifier conflit conducteur sur créneau
    @Query("""
        SELECT COUNT(a) > 0 FROM Affectation a
        WHERE a.conducteur.id = :conducteurId
        AND a.statut = 'ACTIVE'
        AND a.dateDebut < :dateFin
        AND a.dateFin > :dateDebut
    """)
    boolean existsConflitConducteur(
            @Param("conducteurId") UUID conducteurId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin")      LocalDateTime dateFin
    );

    // Vérifier conflit bus sur créneau
    @Query("""
        SELECT COUNT(a) > 0 FROM Affectation a
        WHERE a.bus.id = :busId
        AND a.statut = 'ACTIVE'
        AND a.dateDebut < :dateFin
        AND a.dateFin > :dateDebut
    """)
    boolean existsConflitBus(
            @Param("busId")     UUID busId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin")   LocalDateTime dateFin
    );
}