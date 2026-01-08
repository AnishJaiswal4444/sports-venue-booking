package com.venue.sports_venue_booking.controller;

import com.venue.sports_venue_booking.dto.request.SlotRequest;
import com.venue.sports_venue_booking.dto.response.SlotResponse;
import com.venue.sports_venue_booking.endpoint.SlotEndpoint;
import com.venue.sports_venue_booking.service.SlotService;
import com.venue.sports_venue_booking.utils.ResponseUtils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/venues/{venueId}/slots")
@RequiredArgsConstructor
public class SlotController implements SlotEndpoint {

    private final SlotService slotService;

    @Override
    public ResponseEntity<ApiResponse<SlotResponse>> createSlot(Long venueId, SlotRequest request) {
        SlotResponse response = slotService.createSlot(venueId, request);
        return new ResponseEntity<>(
                ApiResponse.success(response, "Slot created successfully"),
                HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<ApiResponse<List<SlotResponse>>> getSlotsByVenue(Long venueId) {
        List<SlotResponse> response = slotService.getSlotsByVenueId(venueId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Slots retrieved successfully")
        );
    }

}