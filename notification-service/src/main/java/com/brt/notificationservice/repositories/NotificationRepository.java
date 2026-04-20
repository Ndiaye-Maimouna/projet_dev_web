package com.brt.notificationservice.repositories;

import com.brt.notificationservice.entities.Notification;
import com.brt.notificationservice.enums.NotificationType;
import com.brt.notificationservice.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Notification> findByUserTypeOrderByCreatedAtDesc(UserType userType);
    List<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type);
    List<Notification> findAllByOrderByCreatedAtDesc();
}
