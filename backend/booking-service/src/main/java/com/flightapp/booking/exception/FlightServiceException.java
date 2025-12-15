package com.flightapp.booking.exception;

public class FlightServiceException extends RuntimeException {
    public FlightServiceException(String message) {
        super(message);
    }

    public FlightServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}