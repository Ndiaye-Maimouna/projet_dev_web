package sn.ept.ticketing_payment_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.ept.ticketing_payment_service.entities.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByPassengerId(String passengerId);
    Optional<Ticket> findByIdAndPassengerId(UUID id, String passengerId);
    List<Ticket> findByLineId(String lineId);
}
