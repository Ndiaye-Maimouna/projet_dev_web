package sn.ept.ticketing_payment_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.ept.ticketing_payment_service.entities.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByPassengerId(String passengerId);
    Optional<Payment> findByTicketId(UUID ticketId);
}
