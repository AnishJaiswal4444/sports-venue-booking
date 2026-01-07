package com.venue.sports_venue_booking.config;

import com.venue.sports_venue_booking.service.SportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupDataLoader implements ApplicationRunner {

    private final SportService sportService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking if sports need to be synced on startup...");
        try {
            int syncedCount = sportService.syncSportsFromExternalApi();
            log.info("Startup sync completed: {} sports synced", syncedCount);
        } catch (Exception e) {
            log.error("Failed to sync sports on startup", e);
        }
    }
}
