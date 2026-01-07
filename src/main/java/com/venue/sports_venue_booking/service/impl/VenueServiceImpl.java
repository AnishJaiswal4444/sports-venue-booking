package com.venue.sports_venue_booking.service.impl;

import com.venue.sports_venue_booking.dto.request.VenueRequest;
import com.venue.sports_venue_booking.dto.response.VenueResponse;
import com.venue.sports_venue_booking.entity.Venue;
import com.venue.sports_venue_booking.exception.DuplicateResourceException;
import com.venue.sports_venue_booking.exception.ResourceNotFoundException;
import com.venue.sports_venue_booking.repository.VenueRepository;
import com.venue.sports_venue_booking.service.VenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

    @Override
    @Transactional
    public VenueResponse createVenue(VenueRequest request) {
        log.info("Creating venue with name: {}", request.getName());

        // Check if venue name already exists
        if (venueRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Venue", "name", request.getName());
        }

        Venue venue = Venue.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .contactPhone(request.getContactPhone())
                .contactEmail(request.getContactEmail())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        Venue savedVenue = venueRepository.save(venue);
        log.info("Venue created successfully with id: {}", savedVenue.getId());

        return mapToResponse(savedVenue);
    }

    @Override
    @Transactional(readOnly = true)
    public VenueResponse getVenueById(Long id) {
        log.info("Fetching venue with id: {}", id);
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", id));
        return mapToResponse(venue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueResponse> getAllVenues() {
        log.info("Fetching all venues");
        return venueRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueResponse> getActiveVenues() {
        log.info("Fetching active venues");
        return venueRepository.findAllActiveVenues()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VenueResponse updateVenue(Long id, VenueRequest request) {
        log.info("Updating venue with id: {}", id);

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", id));

        // Check if name is being changed and if new name already exists
        if (!venue.getName().equals(request.getName()) &&
                venueRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("Venue", "name", request.getName());
        }

        venue.setName(request.getName());
        venue.setLocation(request.getLocation());
        venue.setDescription(request.getDescription());
        venue.setContactPhone(request.getContactPhone());
        venue.setContactEmail(request.getContactEmail());
        venue.setIsActive(request.getIsActive());

        Venue updatedVenue = venueRepository.save(venue);
        log.info("Venue updated successfully with id: {}", updatedVenue.getId());

        return mapToResponse(updatedVenue);
    }

    @Override
    @Transactional
    public void deleteVenue(Long id) {
        log.info("Deleting venue with id: {}", id);

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", id));

        // TODO: Later check if venue has active slots/bookings before deleting
        // For now, just delete directly

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

    private VenueResponse mapToResponse(Venue venue) {
        return VenueResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .location(venue.getLocation())
                .description(venue.getDescription())
                .contactPhone(venue.getContactPhone())
                .contactEmail(venue.getContactEmail())
                .isActive(venue.getIsActive())
                .createdAt(venue.getCreatedAt())
                .updatedAt(venue.getUpdatedAt())
                .build();
    }
}