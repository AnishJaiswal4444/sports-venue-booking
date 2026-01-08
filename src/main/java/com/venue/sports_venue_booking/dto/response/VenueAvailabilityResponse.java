package com.venue.sports_venue_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueAvailabilityResponse {

    private Long venueId;
    private String venueName;
    private String venueLocation;
    private String contactPhone;
    private String contactEmail;
    private Integer totalAvailableSlots;
    private List<SlotSummaryResponse> availableSlots;
}