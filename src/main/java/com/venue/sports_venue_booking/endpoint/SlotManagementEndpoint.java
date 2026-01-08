package com.venue.sports_venue_booking.endpoint;

import com.venue.sports_venue_booking.dto.request.SlotRequest;
import com.venue.sports_venue_booking.dto.response.SlotResponse;
import com.venue.sports_venue_booking.utils.ResponseUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface SlotManagementEndpoint {

    @GetMapping("/{id}")
    ResponseEntity<ResponseUtils.ApiResponse<SlotResponse>> getSlotById(
            @PathVariable("id") Long id
    );

    @PutMapping("/{id}")
    ResponseEntity<ResponseUtils.ApiResponse<SlotResponse>> updateSlot(
            @PathVariable("id") Long id,
            @Valid @RequestBody SlotRequest request
    );

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseUtils.ApiResponse<Void>> deleteSlot(
            @PathVariable("id") Long id
    );
}
