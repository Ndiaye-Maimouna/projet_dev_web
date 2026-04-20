package sn.ept.ticketing_payment_service.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.ept.ticketing_payment_service.dtos.AbonnementRequest;
import sn.ept.ticketing_payment_service.dtos.AbonnementResponse;
import sn.ept.ticketing_payment_service.services.AbonnementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/subscriptions")
public class AbonnementController {

    private final AbonnementService abonnementService;

    @PostMapping
    public ResponseEntity<AbonnementResponse> createSubscription(
            @RequestBody AbonnementRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.accepted()
                .body(abonnementService.createSubscription(request, userId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AbonnementResponse>> getMySubscription(
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(abonnementService.getMyAbonnements(userId));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateSubscription(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId
    ) {
        abonnementService.deactivateSubscription(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbonnementResponse> getSubscription(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(abonnementService.getSubscription(id, userId));
    }

    @GetMapping
    public ResponseEntity<List<AbonnementResponse>> getAllSubscriptions(
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(abonnementService.getAllAbonnements());
    }
}
