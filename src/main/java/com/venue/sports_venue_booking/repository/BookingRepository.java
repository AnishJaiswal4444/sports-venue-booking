package com.venue.sports_venue_booking.repository;

import com.venue.sports_venue_booking.entity.Booking;
import com.venue.sports_venue_booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerEmailOrderByBookingDateDesc(String customerEmail);

    List<Booking> findByStatusOrderByBookingDateDesc(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.slot.id = :slotId")
    Optional<Booking> findBySlotId(@Param("slotId") Long slotId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b WHERE b.slot.id = :slotId AND b.status = 'CONFIRMED'")
    boolean existsBySlotIdAndStatusConfirmed(@Param("slotId") Long slotId);
}