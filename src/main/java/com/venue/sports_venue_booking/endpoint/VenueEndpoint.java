package com.venue.sports_venue_booking.endpoint;

import com.venue.sports_venue_booking.dto.request.VenueRequest;
import com.venue.sports_venue_booking.dto.response.VenueResponse;
import com.venue.sports_venue_booking.utils.ResponseUtils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface VenueEndpoint {

    @PostMapping
    ResponseEntity<ApiResponse<VenueResponse>> createVenue(@Valid @RequestBody VenueRequest request);

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<VenueResponse>> getVenueById(@PathVariable Long id);

    @GetMapping
    ResponseEntity<ApiResponse<List<VenueResponse>>> getAllVenues();

    @GetMapping("/active")
    ResponseEntity<ApiResponse<List<VenueResponse>>> getActiveVenues();

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<VenueResponse>> updateVenue(
            @PathVariable Long id,
            @Valid @RequestBody VenueRequest request
    );

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteVenue(@PathVariable Long id);

    @PatchMapping("/{id}/deactivate")
    ResponseEntity<ApiResponse<Void>> deactivateVenue(@PathVariable Long id);
}