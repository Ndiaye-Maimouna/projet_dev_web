package com.brt.operationservice.repository;

import com.brt.operationservice.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusRepository extends JpaRepository<Bus, UUID> {
    List<Bus> findByStatut(String statut);
    Optional<Bus> findByImmatriculation(String immatriculation);
}