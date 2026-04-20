package com.brt.operationservice.repository;

import com.brt.operationservice.entity.LigneBRT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LigneBRTRepository extends JpaRepository<LigneBRT, UUID> {
    Optional<LigneBRT> findByNom(String nom);
    List<LigneBRT> findByTerminusDepart(String terminusDepart);
}