package com.venue.sports_venue_booking.service.impl;

import com.venue.sports_venue_booking.dto.request.SlotRequest;
import com.venue.sports_venue_booking.dto.response.SlotResponse;
import com.venue.sports_venue_booking.entity.Slot;
import com.venue.sports_venue_booking.entity.Sport;
import com.venue.sports_venue_booking.entity.Venue;
import com.venue.sports_venue_booking.exception.InvalidSlotTimeException;
import com.venue.sports_venue_booking.exception.ResourceNotFoundException;
import com.venue.sports_venue_booking.exception.SlotOverlapException;
import com.venue.sports_venue_booking.repository.SlotRepository;
import com.venue.sports_venue_booking.repository.SportRepository;
import com.venue.sports_venue_booking.repository.VenueRepository;
import com.venue.sports_venue_booking.service.SlotService;
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
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final VenueRepository venueRepository;
    private final SportRepository sportRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public SlotResponse createSlot(Long venueId, SlotRequest request) {
        log.info("Creating slot for venue {} with sport {} from {} to {}",
                venueId, request.getSportId(), request.getStartTime(), request.getEndTime());

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", venueId));

        Sport sport = sportRepository.findById(request.getSportId())
                .orElseThrow(() -> new ResourceNotFoundException("Sport", "id", request.getSportId()));

        validateSlotTime(request.getStartTime(), request.getEndTime());

        List<Slot> overlappingSlots = slotRepository.findOverlappingSlots(
                venueId,
                request.getSportId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!overlappingSlots.isEmpty()) {
            Slot existingSlot = overlappingSlots.get(0);
            throw new SlotOverlapException(
                    String.format("Slot overlaps with existing %s slot (ID: %d) from %s to %s",
                            sport.getSportName(),
                            existingSlot.getId(),
                            existingSlot.getStartTime(),
                            existingSlot.getEndTime())
            );
        }

        Slot slot = Slot.builder()
                .venue(venue)
                .sport(sport)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .isBooked(false)
                .build();

        Slot savedSlot = slotRepository.save(slot);
        log.info("Slot created successfully with id: {}", savedSlot.getId());

        return mapToResponse(savedSlot);
    }


    @Override
    @Transactional(readOnly = true)
    public SlotResponse getSlotById(Long id) {
        log.info("Fetching slot with id: {}", id);
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", "id", id));
        return mapToResponse(slot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotResponse> getSlotsByVenueId(Long venueId) {
        log.info("Fetching all slots for venue: {}", venueId);

        venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", venueId));

        return slotRepository.findByVenueId(venueId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SlotResponse updateSlot(Long id, SlotRequest request) {
        log.info("Updating slot with id: {}", id);

        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", "id", id));

        if (slot.getIsBooked()) {
            throw new InvalidSlotTimeException("Cannot update a slot that is already booked");
        }

        Sport sport = sportRepository.findById(request.getSportId())
                .orElseThrow(() -> new ResourceNotFoundException("Sport", "id", request.getSportId()));

        validateSlotTime(request.getStartTime(), request.getEndTime());

        List<Slot> overlappingSlots = slotRepository.findOverlappingSlotsExcludingId(
                slot.getVenue().getId(),
                request.getSportId(),
                id,
                request.getStartTime(),
                request.getEndTime()
        );

        if (!overlappingSlots.isEmpty()) {
            Slot existingSlot = overlappingSlots.get(0);
            throw new SlotOverlapException(
                    String.format("Slot overlaps with existing %s slot (ID: %d) from %s to %s",
                            sport.getSportName(),
                            existingSlot.getId(),
                            existingSlot.getStartTime(),
                            existingSlot.getEndTime())
            );
        }

        slot.setSport(sport);
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setPrice(request.getPrice());

        Slot updatedSlot = slotRepository.save(slot);
        log.info("Slot updated successfully with id: {}", updatedSlot.getId());

        return mapToResponse(updatedSlot);
    }

    @Override
    @Transactional
    public void deleteSlot(Long id) {
        log.info("Deleting slot with id: {}", id);

        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", "id", id));

        if (slot.getIsBooked()) {
            throw new InvalidSlotTimeException("Cannot delete a slot that is already booked");
        }

        slotRepository.delete(slot);
        log.info("Slot deleted successfully with id: {}", id);
    }

    private void validateSlotTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new InvalidSlotTimeException(
                    "Start time must be before end time. Start: " + startTime + ", End: " + endTime
            );
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new InvalidSlotTimeException(
                    "Start time must be in the future. Provided: " + startTime
            );
        }

        long durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if (durationMinutes < 30) {
            throw new InvalidSlotTimeException(
                    "Slot duration must be at least 30 minutes. Provided duration: " + durationMinutes + " minutes"
            );
        }

        if (durationMinutes > 1440) {
            throw new InvalidSlotTimeException(
                    "Slot duration cannot exceed 24 hours. Provided duration: " + durationMinutes + " minutes"
            );
        }
    }

    private SlotResponse mapToResponse(Slot slot) {
        SlotResponse response = modelMapper.map(slot, SlotResponse.class);

        response.setVenueId(slot.getVenue().getId());
        response.setVenueName(slot.getVenue().getName());
        response.setVenueLocation(slot.getVenue().getLocation());

        response.setSportId(slot.getSport().getId());
        response.setSportName(slot.getSport().getSportName());

        response.setDurationInMinutes(slot.getDurationInMinutes());
        response.setIsAvailable(slot.isAvailable());

        return response;
    }
}