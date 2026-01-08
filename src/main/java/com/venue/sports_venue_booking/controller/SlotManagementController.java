package com.venue.sports_venue_booking.controller;

import com.venue.sports_venue_booking.dto.request.SlotRequest;
import com.venue.sports_venue_booking.dto.response.SlotResponse;
import com.venue.sports_venue_booking.endpoint.SlotManagementEndpoint;
import com.venue.sports_venue_booking.service.SlotService;
import com.venue.sports_venue_booking.utils.ResponseUtils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotManagementController implements SlotManagementEndpoint {

    private final SlotService slotService;

   @Override
    public ResponseEntity<ApiResponse<SlotResponse>> getSlotById(@PathVariable Long id) {
        SlotResponse response = slotService.getSlotById(id);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Slot retrieved successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<SlotResponse>> updateSlot(
            @PathVariable Long id,
            @Valid @RequestBody SlotRequest request
    ) {
        SlotResponse response = slotService.updateSlot(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Slot updated successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteSlot(@PathVariable Long id) {
        slotService.deleteSlot(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Slot deleted successfully")
        );
    }
}