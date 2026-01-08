package com.venue.sports_venue_booking.controller;

import com.venue.sports_venue_booking.dto.request.BookingRequest;
import com.venue.sports_venue_booking.dto.response.BookingResponse;
import com.venue.sports_venue_booking.endpoint.BookingEndpoint;
import com.venue.sports_venue_booking.service.BookingService;
import com.venue.sports_venue_booking.utils.ResponseUtils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController implements BookingEndpoint {

    private final BookingService bookingService;

    @Override
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return new ResponseEntity<>(
                ApiResponse.success(response, "Booking created successfully"),
                HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Booking retrieved successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        List<BookingResponse> response = bookingService.getAllBookings();
        return ResponseEntity.ok(
                ApiResponse.success(response, "Bookings retrieved successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByCustomer(String email) {
        List<BookingResponse> response = bookingService.getBookingsByCustomerEmail(email);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Customer bookings retrieved successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(Long id) {
        BookingResponse response = bookingService.cancelBooking(id);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Booking cancelled successfully")
        );
    }
}