package sn.ept.ticketing_payment_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.ept.ticketing_payment_service.clients.OperationClient;
import sn.ept.ticketing_payment_service.clients.PassengerClient;
import sn.ept.ticketing_payment_service.dtos.*;
import sn.ept.ticketing_payment_service.entities.Payment;
import sn.ept.ticketing_payment_service.entities.Ticket;
import sn.ept.ticketing_payment_service.enums.PaymentStatus;
import sn.ept.ticketing_payment_service.enums.TicketStatus;
import sn.ept.ticketing_payment_service.events.TicketValidatedEvent;
import sn.ept.ticketing_payment_service.repositories.PaymentRepository;
import sn.ept.ticketing_payment_service.repositories.TicketRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketingService {

    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final OperationClient operationClient;
    private final KafkaProducerService kafkaProducerService;
    private final PaymentService paymentService;
    private final PassengerClient passengerClient;

    // ── Achat ticket ───────────────────────────────────────────

    public TicketResponse buyTicket(TicketRequest request, String userId) {

        // 1. Vérifier que la ligne existe via Feign
        LigneResponse ligne;
        try {
            ligne = operationClient.getLigneById(UUID.fromString(request.getLineId()));
        } catch (Exception e) {
            throw new RuntimeException("Ligne introuvable : " + request.getLineId());
        }

        // 2. Vérifier que les stations existent via Feign
        try {
            operationClient.getStation(UUID.fromString(request.getDepartureStationId()));
            operationClient.getStation(UUID.fromString(request.getArrivalStationId()));
        } catch (Exception e) {
            throw new RuntimeException("Station introuvable");
        }

        // 3. Recuperer le passager par son userId
        PassengerResponse passenger = getPassengerByUserId(userId);


        String phone = passenger.getPhoneNumber();

        // 4. Calculer le prix
        BigDecimal prix = calculatePrice(
                request.getLineId(),
                request.getDepartureStationId(),
                request.getArrivalStationId()
        );

        // 5. Créer le ticket en PENDING_PAYMENT
        Ticket ticket = Ticket.builder()
                .passengerId(passenger.getId().toString())
                .lineId(request.getLineId())
                .departureStationId(request.getDepartureStationId())
                .arrivalStationId(request.getArrivalStationId())
                .prix(prix)
                .statut(TicketStatus.PENDING_PAYMENT)
                .build();
        ticketRepository.save(ticket);

        // 6. Créer le payment en PENDING
        Payment payment = Payment.builder()
                .passengerId(passenger.getId().toString())
                .ticketId(ticket.getId())
                .montant(prix)
                .methode(request.getPaymentMethod())
                .phoneNumber(phone)
                .statut(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        // 7. Simuler le payment mobile money
        paymentService.simulatePayment(payment.getId());

        return toResponse(ticket, payment.getId().toString());
    }

    // ── Validation ticket à la station ─────────────────────────

    public TicketResponse validateTicket(
            ValidateTicketRequest request, String userId) {

        PassengerResponse passenger = getPassengerByUserId(userId);


        Ticket ticket = ticketRepository
                .findByIdAndPassengerId(
                        UUID.fromString(request.getTicketId()), passenger.getId().toString())
                .orElseThrow(() -> new RuntimeException("Ticket introuvable"));

        if (ticket.getStatut() != TicketStatus.VALID) {
            throw new RuntimeException("Ticket non valide : " + ticket.getStatut());
        }

        if (LocalDateTime.now().isAfter(ticket.getDateExpiration())) {
            ticket.setStatut(TicketStatus.EXPIRED);
            ticketRepository.save(ticket);
            throw new RuntimeException("Ticket expiré");
        }

        // Marquer comme validé
        ticket.setStatut(TicketStatus.VALIDATED);
        ticket.setValidatedAt(LocalDateTime.now());
        ticket.setValidatedStationId(request.getStationId());
        ticketRepository.save(ticket);

        // Publier TicketValidated via Kafka
        kafkaProducerService.publishTicketValidated(
                TicketValidatedEvent.builder()
                        .ticketId(ticket.getId().toString())
                        .passengerId(passenger.getId().toString())
                        .passengerUserId(userId)
                        .stationId(request.getStationId())
                        .lineId(ticket.getLineId())
                        .amount(ticket.getPrix())
                        .timestamp(Instant.now())
                        .build()
        );

        return toResponse(ticket, null);
    }


    // ── Helpers ────────────────────────────────────────────────

    private BigDecimal calculatePrice(String lineId, String from, String to) {
        try {
            UUID lineUUID = UUID.fromString(lineId);
            UUID fromId = UUID.fromString(from);
            UUID toId = UUID.fromString(to);

            List<StationResponse> stations =
                    operationClient.getStationsByLigne(lineUUID);

            StationResponse fromStation = null;
            StationResponse toStation = null;

            for (StationResponse s : stations) {
                if (s.getId().equals(fromId)) {
                    fromStation = s;
                }
                if (s.getId().equals(toId)) {
                    toStation = s;
                }
            }

            if (fromStation == null || toStation == null) {
                return new BigDecimal("500");
            }

            // distance (latitude + longitude)
            double dx = fromStation.getLatitude() - toStation.getLatitude();
            double dy = fromStation.getLongitude() - toStation.getLongitude();

            double distance = Math.sqrt(dx * dx + dy * dy);

            double price = 500 + (distance * 10000);

            return BigDecimal.valueOf(Math.max(price, 500));

        } catch (Exception e) {
            return new BigDecimal("500");
        }
    }

    private TicketResponse toResponse(Ticket ticket, String paymentId) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .passengerId(ticket.getPassengerId())
                .lineId(ticket.getLineId())
                .departureStationId(ticket.getDepartureStationId())
                .arrivalStationId(ticket.getArrivalStationId())
                .prix(ticket.getPrix())
                .statut(ticket.getStatut().name())
                .qrCode(ticket.getQrCode())
                .dateAchat(ticket.getDateAchat())
                .dateExpiration(ticket.getDateExpiration())
                .paymentId(paymentId)
                .build();
    }


    public TicketResponse getTicket(UUID id, String userId) {
        PassengerResponse passenger = getPassengerByUserId(userId);

        return toResponse(
                ticketRepository.findByIdAndPassengerId(id, passenger.getId().toString())
                        .orElseThrow(() -> new RuntimeException("Ticket introuvable")),
                null
        );
    }

    public List<TicketResponse> getMyTickets(String userId) {
        PassengerResponse passenger = getPassengerByUserId(userId);

        return ticketRepository.findByPassengerId(passenger.getId().toString())
                .stream().map(t -> toResponse(t, null)).toList();
    }


    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream().map(t -> toResponse(t, null)).toList();
    }

    public void cancelTicket(UUID id, String userId) {
        PassengerResponse passenger = getPassengerByUserId(userId);

        Ticket ticket = ticketRepository
                .findByIdAndPassengerId(id, passenger.getId().toString())
                .orElseThrow(() -> new RuntimeException("Ticket introuvable"));

        ticket.setStatut(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);
    }

    private PassengerResponse getPassengerByUserId(String userId) {
        try {
            return passengerClient.getByUserId(UUID.fromString(userId));
        } catch (Exception e) {
            throw new RuntimeException("Passager introuvable pour userId: " + userId);
        }
    }


}
