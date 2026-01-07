package com.venue.sports_venue_booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SportsVenueBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportsVenueBookingApplication.class, args);
	}

}
