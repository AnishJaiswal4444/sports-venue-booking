package com.venue.sports_venue_booking.service;

import com.venue.sports_venue_booking.dto.request.BookingRequest;
import com.venue.sports_venue_booking.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request);

    BookingResponse getBookingById(Long id);

    List<BookingResponse> getAllBookings();

    List<BookingResponse> getBookingsByCustomerEmail(String email);

    BookingResponse cancelBooking(Long id);
}