package com.venue.sports_venue_booking.endpoint;

import com.venue.sports_venue_booking.dto.request.BookingRequest;
import com.venue.sports_venue_booking.dto.response.BookingResponse;
import com.venue.sports_venue_booking.utils.ResponseUtils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface BookingEndpoint {

    @PostMapping
    ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request
    );

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable("id") Long id
    );

    @GetMapping
    ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings();

    @GetMapping("/customer/{email}")
    ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByCustomer(
            @PathVariable("email") String email
    );

    @PutMapping("/{id}/cancel")
    ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable("id") Long id
    );
}