package com.brt.passenger.repository;

import com.brt.passenger.domain.model.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, UUID> {

    List<Abonnement> findByPassagerIdAndActifTrue(UUID passagerId);

    List<Abonnement> findByPassagerId(UUID passagerId);
}
