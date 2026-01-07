package com.venue.sports_venue_booking.service;

import com.venue.sports_venue_booking.dto.request.VenueRequest;
import com.venue.sports_venue_booking.dto.response.VenueResponse;

import java.util.List;

public interface VenueService {

    VenueResponse createVenue(VenueRequest request);

    VenueResponse getVenueById(Long id);

    List<VenueResponse> getAllVenues();

    List<VenueResponse> getActiveVenues();

    VenueResponse updateVenue(Long id, VenueRequest request);

    void deleteVenue(Long id);

    void softDeleteVenue(Long id);
}