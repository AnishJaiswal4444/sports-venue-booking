package com.venue.sports_venue_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sports", indexes = {
        @Index(name = "idx_sport_id", columnList = "sportId"),
        @Index(name = "idx_sport_code", columnList = "sportCode")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sport_id", nullable = false, unique = true, length = 50)
    private String sportId;

    @Column(name = "sport_code", length = 50)
    private String sportCode;

    @Column(name = "sport_name", nullable = false)
    private String sportName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
