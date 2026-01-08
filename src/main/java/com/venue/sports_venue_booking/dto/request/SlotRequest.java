package com.venue.sports_venue_booking.dto.request;

import jakarta.validation.constraints.*;
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
public class SlotRequest {

    @NotNull(message = "Sport ID is required")
    private Long sportId;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @DecimalMin(value = "0.0", message = "Price must be zero or positive")
    private BigDecimal price = BigDecimal.ZERO;

    // Custom validation will be in service layer:
    // - startTime < endTime
    // - No overlap with existing slots
}