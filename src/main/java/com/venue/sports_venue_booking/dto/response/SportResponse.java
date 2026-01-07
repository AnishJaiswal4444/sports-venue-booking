package com.venue.sports_venue_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SportResponse {

    private Long id;
    private String sportId;
    private String sportCode;
    private String sportName;
    private String description;
    private LocalDateTime lastSyncedAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
