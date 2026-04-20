package com.brt.passenger.exception;

public class DuplicatePassengerException extends RuntimeException {

    public DuplicatePassengerException(String field, String value) {
        super("Un passager avec " + field + " '" + value + "' existe déjà.");
    }
}
