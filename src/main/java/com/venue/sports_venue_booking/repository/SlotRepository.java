package com.venue.sports_venue_booking.repository;

import com.venue.sports_venue_booking.entity.Slot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Slot s WHERE s.id = :id")
    Optional<Slot> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT s FROM Slot s WHERE s.venue.id = :venueId ORDER BY s.startTime")
    List<Slot> findByVenueId(@Param("venueId") Long venueId);

    @Query("SELECT s FROM Slot s WHERE s.venue.id = :venueId " +
            "AND s.sport.id = :sportId " +
            "AND s.startTime < :endTime AND s.endTime > :startTime")
    List<Slot> findOverlappingSlots(
            @Param("venueId") Long venueId,
            @Param("sportId") Long sportId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT s FROM Slot s WHERE s.venue.id = :venueId " +
            "AND s.sport.id = :sportId " +
            "AND s.id <> :slotId " +
            "AND s.startTime < :endTime AND s.endTime > :startTime")
    List<Slot> findOverlappingSlotsExcludingId(
            @Param("venueId") Long venueId,
            @Param("sportId") Long sportId,
            @Param("slotId") Long slotId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT s FROM Slot s " +
            "WHERE s.isBooked = false " +
            "AND s.startTime >= :now " +
            "AND (:sportId IS NULL OR s.sport.id = :sportId) " +
            "AND s.startTime < :endTime " +
            "AND s.endTime > :startTime " +
            "ORDER BY s.startTime")
    List<Slot> findAvailableSlots(
            @Param("sportId") Long sportId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT COUNT(s) FROM Slot s WHERE s.venue.id = :venueId")
    long countSlotsByVenue(@Param("venueId") Long venueId);
}