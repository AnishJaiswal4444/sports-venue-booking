package com.venue.sports_venue_booking.service.impl;

import com.venue.sports_venue_booking.dto.request.VenueRequest;
import com.venue.sports_venue_booking.dto.response.VenueResponse;
import com.venue.sports_venue_booking.entity.Venue;
import com.venue.sports_venue_booking.exception.DuplicateResourceException;
import com.venue.sports_venue_booking.exception.ResourceNotFoundException;
import com.venue.sports_venue_booking.exception.VenueDeletionException;
import com.venue.sports_venue_booking.repository.SlotRepository;
import com.venue.sports_venue_booking.repository.VenueRepository;
import com.venue.sports_venue_booking.service.VenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final ModelMapper modelMapper;
    private final SlotRepository slotRepository;

    @Override
    @Transactional
    public VenueResponse createVenue(VenueRequest request) {
        log.info("Creating venue with name: {}", request.getName());

        if (venueRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Venue", "name", request.getName());
        }

        Venue venue = modelMapper.map(request, Venue.class);

        Venue savedVenue = venueRepository.save(venue);
        log.info("Venue created successfully with id: {}", savedVenue.getId());

        return modelMapper.map(savedVenue, VenueResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public VenueResponse getVenueById(Long id) {
        log.info("Fetching venue with id: {}", id);
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", id));
        return modelMapper.map(venue, VenueResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueResponse> getAllVenues() {
        log.info("Fetching all venues");
        return venueRepository.findAll()
                .stream()
                .map(venue -> modelMapper.map(venue, VenueResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueResponse> getActiveVenues() {
        log.info("Fetching active venues");
        return venueRepository.findAllActiveVenues()
                .stream()
                .map(venue -> modelMapper.map(venue, VenueResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VenueResponse updateVenue(Long id, VenueRequest request) {
        log.info("Updating venue with id: {}", id);

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", id));

        if (!venue.getName().equals(request.getName()) &&
                venueRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("Venue", "name", request.getName());
        }

        modelMapper.map(request, venue);

        Venue updatedVenue = venueRepository.save(venue);
        log.info("Venue updated successfully with id: {}", updatedVenue.getId());

        return modelMapper.map(updatedVenue, VenueResponse.class);
    }

    @Override
    @Transactional
    public void deleteVenue(Long id) {
        log.info("Deleting venue with id: {}", id);

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", id));

        long totalSlots = slotRepository.countSlotsByVenue(id);

        if (totalSlots > 0) {
            throw new VenueDeletionException(
                    String.format("Cannot delete venue. It has %d slot(s). " +
                            "Please delete all slots first.", totalSlots)
            );
        }
        venueRepository.delete(venue);
        log.info("Venue deleted successfully with id: {}", id);
    }

    @Override
    @Transactional
    public void softDeleteVenue(Long id) {
        log.info("Soft deleting venue with id: {}", id);

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", id));

        venue.setIsActive(false);
        venueRepository.save(venue);

        log.info("Venue soft deleted (marked as inactive) with id: {}", id);
    }
}