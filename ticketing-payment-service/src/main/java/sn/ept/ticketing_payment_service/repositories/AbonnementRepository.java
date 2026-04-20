package sn.ept.ticketing_payment_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.ept.ticketing_payment_service.entities.Abonnement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AbonnementRepository extends JpaRepository<Abonnement, UUID> {
    List<Abonnement> findByPassengerIdAndActifTrue(String passengerId);
}
