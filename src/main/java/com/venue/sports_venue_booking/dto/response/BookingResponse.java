package com.venue.sports_venue_booking.dto.response;

import com.venue.sports_venue_booking.entity.BookingStatus;
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
public class BookingResponse {

    private Long bookingId;

    // Customer details
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Booking details
    private LocalDateTime bookingDate;
    private BookingStatus status;

    // Slot details
    private Long slotId;
    private LocalDateTime slotStartTime;
    private LocalDateTime slotEndTime;
    private BigDecimal slotPrice;

    // Venue details
    private Long venueId;
    private String venueName;
    private String venueLocation;

    // Sport details
    private Long sportId;
    private String sportName;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}