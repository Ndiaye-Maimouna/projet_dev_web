package com.brt.passenger.repository;

import com.brt.passenger.domain.model.TripHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TripHistoryRepository extends JpaRepository<TripHistory, UUID> {

    Page<TripHistory> findByPassagerIdOrderByTripDateDesc(UUID passagerId, Pageable pageable);

    long countByPassagerId(UUID passagerId);
}
