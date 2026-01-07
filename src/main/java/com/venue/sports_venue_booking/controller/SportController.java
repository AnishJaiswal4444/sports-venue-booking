package com.venue.sports_venue_booking.controller;

import com.venue.sports_venue_booking.dto.response.SportResponse;
import com.venue.sports_venue_booking.endpoint.SportEndpoint;
import com.venue.sports_venue_booking.service.SportService;
import com.venue.sports_venue_booking.utils.ResponseUtils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
public class SportController implements SportEndpoint {

    private final SportService sportService;

    @Override
    public ResponseEntity<ApiResponse<List<SportResponse>>> getAllSports() {
        List<SportResponse> response = sportService.getAllSports();
        return ResponseEntity.ok(
                ApiResponse.success(response, "Sports retrieved successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<SportResponse>> getSportById(Long id) {
        SportResponse response = sportService.getSportById(id);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Sport retrieved successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<SportResponse>> getSportBySportId(String sportId) {
        SportResponse response = sportService.getSportBySportId(sportId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Sport retrieved successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<String>> syncSports() {
        int syncedCount = sportService.syncSportsFromExternalApi();
        return ResponseEntity.ok(
                ApiResponse.success(
                        syncedCount + " sports synced successfully",
                        "Sports synchronized from external API"
                )
        );
    }
}