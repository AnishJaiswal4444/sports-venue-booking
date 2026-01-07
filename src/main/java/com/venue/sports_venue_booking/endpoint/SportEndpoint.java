package com.venue.sports_venue_booking.endpoint;

import com.venue.sports_venue_booking.dto.response.SportResponse;
import com.venue.sports_venue_booking.utils.ResponseUtils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

public interface SportEndpoint {

    @GetMapping
    ResponseEntity<ApiResponse<List<SportResponse>>> getAllSports();

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<SportResponse>> getSportById(@PathVariable Long id);

    @GetMapping("/sport-id/{sportId}")
    ResponseEntity<ApiResponse<SportResponse>> getSportBySportId(@PathVariable String sportId);

    @PostMapping("/sync")
    ResponseEntity<ApiResponse<String>> syncSports();
}
