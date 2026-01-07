package com.venue.sports_venue_booking.service;

import com.venue.sports_venue_booking.dto.external.SportsApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class SportsApiClient {

    @Value("${sports.api.url}")
    private String sportsApiUrl;

    private final RestTemplate restTemplate;

    public SportsApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public SportsApiResponse fetchSportsFromExternalApi() {
        log.info("Fetching sports from external API: {}", sportsApiUrl);
        try {
            SportsApiResponse response = restTemplate.getForObject(sportsApiUrl, SportsApiResponse.class);
            log.info("Successfully fetched {} sports from external API",
                    response != null && response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch sports from external API", e);
            throw new RuntimeException("Failed to fetch sports from external API: " + e.getMessage());
        }
    }
}
