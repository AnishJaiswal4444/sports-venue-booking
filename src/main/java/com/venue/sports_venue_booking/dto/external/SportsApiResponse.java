package com.venue.sports_venue_booking.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SportsApiResponse {

    private String status;
    private String msg;
    private String err;
    private List<SportData> data;

    @Data
    public static class SportData {
        @JsonProperty("sport_id")
        private Long sportId;

        @JsonProperty("sport_code")
        private String sportCode;

        @JsonProperty("sport_name")
        private String sportName;
    }
}
