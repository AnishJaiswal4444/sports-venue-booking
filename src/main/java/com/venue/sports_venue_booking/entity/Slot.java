package com.venue.sports_venue_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "slots",
        indexes = {
                @Index(name = "idx_slot_venue_time", columnList = "venue_id, start_time, end_time"),
                @Index(name = "idx_slot_sport", columnList = "sport_id"),
                @Index(name = "idx_slot_availability", columnList = "is_booked, start_time"),
                @Index(name = "idx_slot_search", columnList = "sport_id, start_time, end_time, is_booked")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_slot_venue"))
    private Venue venue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_slot_sport"))
    private Sport sport;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "is_booked", nullable = false)
    @Builder.Default
    private Boolean isBooked = false;

    // Business logic methods
    public boolean isAvailable() {
        return !isBooked && startTime.isAfter(LocalDateTime.now());
    }

    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        // Slots overlap if: (start < otherEnd) AND (end > otherStart)
        return startTime.isBefore(otherEnd) && endTime.isAfter(otherStart);
    }

    public long getDurationInMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }
}