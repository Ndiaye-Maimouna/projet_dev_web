package sn.ept.ticketing_payment_service.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.ept.ticketing_payment_service.dtos.*;
import sn.ept.ticketing_payment_service.services.TicketingService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/tickets")
public class TicketingController {

    private final TicketingService ticketingService;


    @PostMapping
    public ResponseEntity<TicketResponse> buyTicket(
            @RequestBody TicketRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.accepted()
                .body(ticketingService.buyTicket(request, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(ticketingService.getTicket(id, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelTicket(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId
    ) {
        ticketingService.cancelTicket(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<TicketResponse>> getMyTickets(
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(ticketingService.getMyTickets(userId));
    }

    @PostMapping("/validate")
    public ResponseEntity<TicketResponse> validateTicket(
            @RequestBody ValidateTicketRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(
                ticketingService.validateTicket(request, userId));
    }


    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets(
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ticketingService.getAllTickets());
    }
}