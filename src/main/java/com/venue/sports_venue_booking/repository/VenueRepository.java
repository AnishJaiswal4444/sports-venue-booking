package com.venue.sports_venue_booking.repository;

import com.venue.sports_venue_booking.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    Optional<Venue> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Venue> findByIsActive(Boolean isActive);

    @Query("SELECT v FROM Venue v WHERE v.isActive = true ORDER BY v.name")
    List<Venue> findAllActiveVenues();

    @Query("SELECT COUNT(v) FROM Venue v WHERE v.isActive = true")
    long countActiveVenues();
}