package com.venue.sports_venue_booking.repository;

import com.venue.sports_venue_booking.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {

    Optional<Sport> findBySportId(String sportId);

    Optional<Sport> findBySportCode(String sportCode);

    boolean existsBySportId(String sportId);
}