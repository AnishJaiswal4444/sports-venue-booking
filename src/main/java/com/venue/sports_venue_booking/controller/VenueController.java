package com.venue.sports_venue_booking.controller;

import com.venue.sports_venue_booking.dto.request.VenueRequest;
import com.venue.sports_venue_booking.dto.response.VenueResponse;
import com.venue.sports_venue_booking.endpoint.VenueEndpoint;
import com.venue.sports_venue_booking.service.VenueService;
import com.venue.sports_venue_booking.utils.ResponseUtils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController implements VenueEndpoint {

    private final VenueService venueService;

    @Override
    public ResponseEntity<ApiResponse<VenueResponse>> createVenue(VenueRequest request) {
        VenueResponse response = venueService.createVenue(request);
        return new ResponseEntity<>(
                ApiResponse.success(response, "Venue created successfully"),
                HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<ApiResponse<VenueResponse>> getVenueById(Long id) {
        VenueResponse response = venueService.getVenueById(id);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Venue retrieved successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<List<VenueResponse>>> getAllVenues() {
        List<VenueResponse> response = venueService.getAllVenues();
        return ResponseEntity.ok(
                ApiResponse.success(response, "Venues retrieved successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<List<VenueResponse>>> getActiveVenues() {
        List<VenueResponse> response = venueService.getActiveVenues();
        return ResponseEntity.ok(
                ApiResponse.success(response, "Active venues retrieved successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<VenueResponse>> updateVenue(Long id, VenueRequest request) {
        VenueResponse response = venueService.updateVenue(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Venue updated successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteVenue(Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Venue deleted successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deactivateVenue(Long id) {
        venueService.softDeleteVenue(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Venue deactivated successfully")
        );
    }
}