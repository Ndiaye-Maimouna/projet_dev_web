package sn.ept.ticketing_payment_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.ept.ticketing_payment_service.clients.PassengerClient;
import sn.ept.ticketing_payment_service.dtos.PassengerResponse;
import sn.ept.ticketing_payment_service.dtos.PaymentResponse;
import sn.ept.ticketing_payment_service.dtos.WebhookRequest;
import sn.ept.ticketing_payment_service.entities.Payment;
import sn.ept.ticketing_payment_service.entities.Ticket;
import sn.ept.ticketing_payment_service.enums.PaymentStatus;
import sn.ept.ticketing_payment_service.enums.TicketStatus;
import sn.ept.ticketing_payment_service.events.PaymentFailedEvent;
import sn.ept.ticketing_payment_service.events.TicketPurchasedEvent;
import sn.ept.ticketing_payment_service.exceptions.BusinessException;
import sn.ept.ticketing_payment_service.exceptions.ResourceNotFoundException;
import sn.ept.ticketing_payment_service.repositories.PaymentRepository;
import sn.ept.ticketing_payment_service.repositories.TicketRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaProducerService kafkaProducerService;
    private final TicketRepository ticketRepository;
    private final PassengerClient passengerClient;


    public void handleWebhook(WebhookRequest request) {

        Payment payment = paymentRepository
                .findById(UUID.fromString(request.getIdPayment()))
                .orElseThrow(() -> new ResourceNotFoundException("Payment introuvable" + request.getIdPayment()));

        PassengerResponse passenger = passengerClient.getPassager(UUID.fromString(payment.getPassengerId()));

        if (request.getStatus().equals("SUCCESS")) {

            payment.setStatut(PaymentStatus.SUCCESS);
            payment.setOperatorReference(request.getOperatorRef());
            paymentRepository.save(payment);

            Ticket ticket = ticketRepository
                    .findById(payment.getTicketId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket introuvable" +  payment.getTicketId()));

            ticket.setStatut(TicketStatus.VALID);
            ticket.setDateAchat(LocalDateTime.now());
            ticket.setDateExpiration(LocalDateTime.now().plusDays(1));
            ticket.setQrCode("QR-" + ticket.getId().toString().toUpperCase());
            ticketRepository.save(ticket);

            // Publier TicketPurchased via Kafka
            kafkaProducerService.publishTicketPurchased(
                    TicketPurchasedEvent.builder()
                            .ticketId(ticket.getId().toString())
                            .passengerId(ticket.getPassengerId())
                            .passengerUserId(passenger.getPassengerUserId().toString())
                            .lineId(ticket.getLineId())
                            .amount(ticket.getPrix())
                            .timestamp(Instant.now())
                            .build()
            );

        } else {

            payment.setStatut(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            // Publier PaymentFailed via Kafka
            kafkaProducerService.publishPaymentFailed(
                    PaymentFailedEvent.builder()
                            .paymentId(payment.getId().toString())
                            .passengerId(payment.getPassengerId())
                            .passengerUserId(passenger.getPassengerUserId().toString())
                            .reason("Payment refusé par l'opérateur")
                            .timestamp(Instant.now())
                            .build()
            );
        }
    }

    public void simulatePayment(UUID paymentId) {
        WebhookRequest request = new WebhookRequest();
        request.setIdPayment(paymentId.toString());
        request.setStatus("SUCCESS");
        request.setOperatorRef("SIMULATED");

        handleWebhook(request);
    }

    public List<PaymentResponse> getMyPayments(String userId) {
        PassengerResponse passenger = getPassengerByUserId(userId);
        return paymentRepository.findByPassengerId(passenger.getId().toString())
                .stream().map(this::toPaymentResponse).toList();
    }

    public PaymentResponse getPayment(UUID id, String userId) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment introuvable" + id));
        PassengerResponse passenger = getPassengerByUserId(userId);


        if (!payment.getPassengerId().equals(passenger.getId().toString())) {
            throw new BusinessException("Accès refusé");
        }

        return toPaymentResponse(payment);
    }

    public void retryPayment(UUID id, String userId) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment introuvable" + id));
        PassengerResponse passenger = getPassengerByUserId(userId);


        if (!payment.getPassengerId().equals(passenger.getId().toString())) {
            throw new BusinessException("Accès refusé");
        }

        payment.setStatut(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        simulatePayment(payment.getId());
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream().map(this::toPaymentResponse).toList();
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .passengerId(payment.getPassengerId())
                .ticketId(payment.getTicketId())
                .abonnementId(payment.getAbonnementId())
                .montant(payment.getMontant())
                .devise(payment.getDevise())
                .methode(payment.getMethode())
                .statut(payment.getStatut().name())
                .phoneNumber(payment.getPhoneNumber())
                .operatorReference(payment.getOperatorReference())
                .date(payment.getDate())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private PassengerResponse getPassengerByUserId(String userId) {
        try {
            return passengerClient.getByUserId(UUID.fromString(userId));
        } catch (Exception e) {
            throw new ResourceNotFoundException("Passager introuvable pour userId: " + userId);
        }
    }

}
