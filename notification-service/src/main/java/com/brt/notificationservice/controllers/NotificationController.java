package com.brt.notificationservice.controllers;

import com.brt.notificationservice.entities.Notification;
import com.brt.notificationservice.enums.NotificationType;
import com.brt.notificationservice.enums.UserType;
import com.brt.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    // Mes notifications (passager ou conducteur connecté)
    @GetMapping("/my")
    public ResponseEntity<List<Notification>> getMyNotifications(
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(notificationService.getByUserId(userId));
    }

    // Toutes les notifications d'un type — ADMIN
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getByType(
            @PathVariable NotificationType type,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(notificationService.getByType(type));
    }

    // Toutes les notifications des passagers — ADMIN
    @GetMapping("/passengers")
    public ResponseEntity<List<Notification>> getPassengerNotifications(
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                notificationService.getByUserType(UserType.PASSENGER));
    }

    // Toutes les notifications des conducteurs — ADMIN
    @GetMapping("/drivers")
    public ResponseEntity<List<Notification>> getDriverNotifications(
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                notificationService.getByUserType(UserType.DRIVER));
    }

    // Toutes les notifications — ADMIN
    @GetMapping
    public ResponseEntity<List<Notification>> getAll(
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(notificationService.getAll());
    }
}
