package com.venue.sports_venue_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotResponse {

    private Long id;

    // Venue details
    private Long venueId;
    private String venueName;
    private String venueLocation;

    // Sport details
    private Long sportId;
    private String sportName;

    // Slot details
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationInMinutes;
    private BigDecimal price;
    private Boolean isBooked;
    private Boolean isAvailable;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}