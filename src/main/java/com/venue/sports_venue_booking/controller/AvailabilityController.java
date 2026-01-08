package com.venue.sports_venue_booking.controller;

import com.venue.sports_venue_booking.dto.response.VenueAvailabilityResponse;
import com.venue.sports_venue_booking.endpoint.AvailabilityEndpoint;
import com.venue.sports_venue_booking.service.AvailabilityService;
import com.venue.sports_venue_booking.utils.ResponseUtils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class AvailabilityController implements AvailabilityEndpoint {

    private final AvailabilityService availabilityService;

    @Override
    public ResponseEntity<ApiResponse<List<VenueAvailabilityResponse>>> getAvailableVenues(
            Long sportId, LocalDateTime startTime, LocalDateTime endTime) {
        List<VenueAvailabilityResponse> response = availabilityService.getAvailableVenues(
                sportId,
                startTime,
                endTime
        );
        return ResponseEntity.ok(
                ApiResponse.success(response, "Available venues retrieved successfully")
        );
    }
}