package com.brt.notificationservice.service;

import com.brt.notificationservice.entities.Notification;
import com.brt.notificationservice.enums.NotificationStatut;
import com.brt.notificationservice.enums.NotificationType;
import com.brt.notificationservice.enums.UserType;
import com.brt.notificationservice.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification create(Notification notification) {
        notification.setDateEnvoi(LocalDateTime.now());
        notification.setStatut(NotificationStatut.ENVOYE);
        return notificationRepository.save(notification);
    }

    public List<Notification> getByUserId(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getByUserType(UserType userType) {
        return notificationRepository.findByUserTypeOrderByCreatedAtDesc(userType);
    }

    public List<Notification> getByType(NotificationType type) {
        return notificationRepository.findByTypeOrderByCreatedAtDesc(type);
    }

    public List<Notification> getAll() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }
}