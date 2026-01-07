package com.venue.sports_venue_booking.service;

import com.venue.sports_venue_booking.dto.response.SportResponse;

import java.util.List;

public interface SportService {

    List<SportResponse> getAllSports();

    SportResponse getSportById(Long id);

    SportResponse getSportBySportId(String sportId);

    int syncSportsFromExternalApi();
}