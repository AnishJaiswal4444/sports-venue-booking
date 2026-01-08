package com.venue.sports_venue_booking.exception;

public class SlotOverlapException extends RuntimeException {
    public SlotOverlapException(String message) {
        super(message);
    }
}
