package com.venue.sports_venue_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venues",
        indexes = {
                @Index(name = "idx_venue_name", columnList = "name"),
                @Index(name = "idx_venue_location", columnList = "location"),
                @Index(name = "idx_venue_active", columnList = "isActive")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, length = 500)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // Relationship with Slot (we'll add this later when creating Slot entity)
    // @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    // @Builder.Default
    // private List<Slot> slots = new ArrayList<>();
}