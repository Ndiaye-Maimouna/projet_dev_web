package com.brt.passenger.repository;

import com.brt.passenger.domain.model.Passager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassengerRepository extends JpaRepository<Passager, UUID> {

    Optional<Passager> findByEmail(String email);

    Optional<Passager> findByUserId(UUID userId);

    boolean existsByEmail(String email);

    boolean existsByTelephone(String telephone);

    @Query("""
        SELECT p FROM Passager p
        WHERE LOWER(p.prenom)    LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(p.nom)       LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(p.email)     LIKE LOWER(CONCAT('%', :query, '%'))
           OR p.telephone        LIKE CONCAT('%', :query, '%')
    """)
    Page<Passager> search(@Param("query") String query, Pageable pageable);
}
