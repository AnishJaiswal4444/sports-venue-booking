package com.venue.sports_venue_booking.service.impl;

import com.venue.sports_venue_booking.dto.external.SportsApiResponse;
import com.venue.sports_venue_booking.dto.response.SportResponse;
import com.venue.sports_venue_booking.entity.Sport;
import com.venue.sports_venue_booking.exception.ResourceNotFoundException;
import com.venue.sports_venue_booking.repository.SportRepository;
import com.venue.sports_venue_booking.service.SportService;
import com.venue.sports_venue_booking.service.impl.SportsApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SportServiceImpl implements SportService {

    private final SportRepository sportRepository;
    private final SportsApiClient sportsApiClient;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SportResponse> getAllSports() {
        log.info("Fetching all sports from database");
        return sportRepository.findAll()
                .stream()
                .map(sport -> modelMapper.map(sport, SportResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SportResponse getSportById(Long id) {
        log.info("Fetching sport with id: {}", id);
        Sport sport = sportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sport", "id", id));
        return modelMapper.map(sport, SportResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public SportResponse getSportBySportId(String sportId) {
        log.info("Fetching sport with sportId: {}", sportId);
        Sport sport = sportRepository.findBySportId(sportId)
                .orElseThrow(() -> new ResourceNotFoundException("Sport", "sportId", sportId));
        return modelMapper.map(sport, SportResponse.class);
    }

    @Override
    @Transactional
    public int syncSportsFromExternalApi() {
        log.info("Starting sports synchronization from external API");

        SportsApiResponse apiResponse = sportsApiClient.fetchSportsFromExternalApi();

        if (apiResponse == null || apiResponse.getData() == null) {
            log.warn("No sports data received from external API");
            return 0;
        }

        int syncedCount = 0;
        for (SportsApiResponse.SportData sportData : apiResponse.getData()) {
            try {
                Sport sport = sportRepository.findBySportId(String.valueOf(sportData.getSportId()))
                        .orElse(null);

                if (sport == null) {
                    sport = Sport.builder()
                            .sportId(String.valueOf(sportData.getSportId()))
                            .sportCode(sportData.getSportCode())
                            .sportName(sportData.getSportName())
                            .isActive(true)
                            .lastSyncedAt(LocalDateTime.now())
                            .build();
                    log.info("Creating new sport: {}", sportData.getSportName());
                } else {
                    sport.setSportCode(sportData.getSportCode());
                    sport.setSportName(sportData.getSportName());
                    sport.setLastSyncedAt(LocalDateTime.now());
                    log.info("Updating existing sport: {}", sportData.getSportName());
                }

                sportRepository.save(sport);
                syncedCount++;
            } catch (Exception e) {
                log.error("Failed to sync sport: {}", sportData.getSportName(), e);
            }
        }

        log.info("Successfully synced {} sports from external API", syncedCount);
        return syncedCount;
    }
}