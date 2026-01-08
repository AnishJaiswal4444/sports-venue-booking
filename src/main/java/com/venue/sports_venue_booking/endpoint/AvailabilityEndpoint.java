package com.venue.sports_venue_booking.endpoint;

import com.venue.sports_venue_booking.dto.response.VenueAvailabilityResponse;
import com.venue.sports_venue_booking.utils.ResponseUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilityEndpoint {
    @GetMapping("/available")
    public ResponseEntity<ResponseUtils.ApiResponse<List<VenueAvailabilityResponse>>> getAvailableVenues(
            @RequestParam(required = false) Long sportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    );
}
