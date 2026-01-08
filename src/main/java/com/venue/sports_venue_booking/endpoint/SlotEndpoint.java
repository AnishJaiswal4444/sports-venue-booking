package com.venue.sports_venue_booking.endpoint;

import com.venue.sports_venue_booking.dto.request.SlotRequest;
import com.venue.sports_venue_booking.dto.response.SlotResponse;
import com.venue.sports_venue_booking.utils.ResponseUtils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface SlotEndpoint {

    @PostMapping  // Maps to: POST /api/venues/{venueId}/slots
    ResponseEntity<ApiResponse<SlotResponse>> createSlot(
            @PathVariable("venueId") Long venueId,
            @Valid @RequestBody SlotRequest request
    );

    @GetMapping  // Maps to: GET /api/venues/{venueId}/slots
    ResponseEntity<ApiResponse<List<SlotResponse>>> getSlotsByVenue(
            @PathVariable("venueId") Long venueId
    );

}
