package com.brt.passenger.exception;

import java.util.UUID;

// ── Exception de base ──────────────────────────────────────────────
class BrtException extends RuntimeException {
    public BrtException(String message) { super(message); }
}

// ── Passenger not found ────────────────────────────────────────────
public class PassengerNotFoundException extends BrtException {

    public PassengerNotFoundException(UUID id) {
        super("Passager introuvable avec l'ID : " + id);
    }

    public PassengerNotFoundException(String field, String value) {
        super("Passager introuvable avec " + field + " : " + value);
    }
}
