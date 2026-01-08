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
public class SlotSummaryResponse {

    private Long slotId;
    private Long sportId;
    private String sportName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationInMinutes;
    private BigDecimal price;
}