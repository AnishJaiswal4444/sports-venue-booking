package com.venue.sports_venue_booking.service.impl;

import com.venue.sports_venue_booking.dto.response.SlotSummaryResponse;
import com.venue.sports_venue_booking.dto.response.VenueAvailabilityResponse;
import com.venue.sports_venue_booking.entity.Slot;
import com.venue.sports_venue_booking.entity.Venue;
import com.venue.sports_venue_booking.repository.SlotRepository;
import com.venue.sports_venue_booking.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityServiceImpl implements AvailabilityService {

    private final SlotRepository slotRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VenueAvailabilityResponse> getAvailableVenues(
            Long sportId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        log.info("Searching available venues - Sport: {}, Time: {} to {}",
                sportId, startTime, endTime);

        List<Slot> availableSlots = slotRepository.findAvailableSlots(
                sportId,
                startTime,
                endTime,
                LocalDateTime.now()
        );

        if (availableSlots.isEmpty()) {
            log.info("No available slots found");
            return List.of();
        }

        Map<Venue, List<Slot>> slotsByVenue = availableSlots.stream()
                .collect(Collectors.groupingBy(Slot::getVenue));


        List<VenueAvailabilityResponse> response = slotsByVenue.entrySet().stream()
                .map(entry -> {
                    Venue venue = entry.getKey();
                    List<Slot> slots = entry.getValue();

                    return VenueAvailabilityResponse.builder()
                            .venueId(venue.getId())
                            .venueName(venue.getName())
                            .venueLocation(venue.getLocation())
                            .contactPhone(venue.getContactPhone())
                            .contactEmail(venue.getContactEmail())
                            .totalAvailableSlots(slots.size())
                            .availableSlots(slots.stream()
                                    .map(this::mapToSlotSummary)
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());

        log.info("Found {} venues with available slots", response.size());
        return response;
    }

    private SlotSummaryResponse mapToSlotSummary(Slot slot) {
        return SlotSummaryResponse.builder()
                .slotId(slot.getId())
                .sportId(slot.getSport().getId())
                .sportName(slot.getSport().getSportName())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .durationInMinutes(slot.getDurationInMinutes())
                .price(slot.getPrice())
                .build();
    }
}