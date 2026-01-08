package com.venue.sports_venue_booking.service;

import com.venue.sports_venue_booking.dto.request.AvailabilityRequest;
import com.venue.sports_venue_booking.dto.response.VenueAvailabilityResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilityService {

    List<VenueAvailabilityResponse> getAvailableVenues(
            Long sportId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}