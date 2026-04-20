package com.brt.gateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Contrôleur de fallback pour les Circuit Breakers.
 *
 * Quand un microservice est indisponible, le gateway renvoie
 * une réponse claire au client plutôt qu'une erreur 500 générique.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/passenger")
    public Mono<ResponseEntity<Map<String, Object>>> passengerFallback() {
        return buildFallback("passenger-service", "Le service passager est temporairement indisponible.");
    }

    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        return buildFallback("auth-service", "Le service d'authentification est temporairement indisponible.");
    }

    @GetMapping("/operation")
    public Mono<ResponseEntity<Map<String, Object>>> operationFallback() {
        return buildFallback("operation-service", "Le service d'exploitation est temporairement indisponible.");
    }

    @GetMapping("/ticketing")
    public Mono<ResponseEntity<Map<String, Object>>> ticketingFallback() {
        return buildFallback("ticketing-service", "Le service de billetterie est temporairement indisponible.");
    }

    @GetMapping("/tracking")
    public Mono<ResponseEntity<Map<String, Object>>> trackingFallback() {
        return buildFallback("realtime-tracking-service", "Le service de tracking est temporairement indisponible.");
    }

    @GetMapping("/fleet")
    public Mono<ResponseEntity<Map<String, Object>>> fleetFallback() {
        return buildFallback("fleet-management-service", "Le service de gestion de flotte est temporairement indisponible.");
    }

    // ── Helper ────────────────────────────────────────────────────────

    private Mono<ResponseEntity<Map<String, Object>>> buildFallback(String service, String message) {
        Map<String, Object> body = Map.of(
                "status",    503,
                "error",     "Service Unavailable",
                "service",   service,
                "message",   message,
                "suggestion","Veuillez réessayer dans quelques instants.",
                "timestamp", Instant.now().toString()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }
}
