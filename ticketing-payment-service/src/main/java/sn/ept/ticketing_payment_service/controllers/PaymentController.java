package sn.ept.ticketing_payment_service.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.ept.ticketing_payment_service.dtos.PaymentResponse;
import sn.ept.ticketing_payment_service.dtos.TicketResponse;
import sn.ept.ticketing_payment_service.dtos.WebhookRequest;
import sn.ept.ticketing_payment_service.services.PaymentService;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(paymentService.getPayment(id, userId));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody WebhookRequest request
    ) {
        paymentService.handleWebhook(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<Void> retryPayment(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId
    ) {
        paymentService.retryPayment(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(paymentService.getMyPayments(userId));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments(
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}
