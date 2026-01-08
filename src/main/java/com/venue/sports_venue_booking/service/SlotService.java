package com.venue.sports_venue_booking.service;

import com.venue.sports_venue_booking.dto.request.SlotRequest;
import com.venue.sports_venue_booking.dto.response.SlotResponse;

import java.util.List;

public interface SlotService {

    SlotResponse createSlot(Long venueId, SlotRequest request);

    SlotResponse getSlotById(Long id);

    List<SlotResponse> getSlotsByVenueId(Long venueId);

    SlotResponse updateSlot(Long id, SlotRequest request);

    void deleteSlot(Long id);
}