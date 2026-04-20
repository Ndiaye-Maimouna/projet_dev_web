package sn.ept.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.ept.security_service.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
