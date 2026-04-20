package com.brt.fleetmanagementservice.repository;

import com.brt.fleetmanagementservice.entity.Conducteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConducteurRepository extends JpaRepository<Conducteur, UUID> {
    List<Conducteur> findByStatut(String statut);
    Optional<Conducteur> findByNumeroPermis(String numeroPermis);
    Optional<Conducteur> findByEmail(String email);
}
