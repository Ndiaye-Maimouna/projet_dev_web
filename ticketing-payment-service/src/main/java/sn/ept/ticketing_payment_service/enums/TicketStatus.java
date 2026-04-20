package sn.ept.ticketing_payment_service.enums;

public enum TicketStatus {
    PENDING_PAYMENT,  // payment en attente
    VALID,            // payé et valide
    VALIDATED,        // utilisé à la station
    EXPIRED,          // expiré
    CANCELLED         // annulé
}