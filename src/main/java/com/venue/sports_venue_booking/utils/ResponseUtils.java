package com.venue.sports_venue_booking.utils;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ResponseUtils {

    private ResponseUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        @Builder.Default
        private LocalDateTime timestamp = LocalDateTime.now();

        public static <T> ApiResponse<T> success(T data, String message) {
            return ApiResponse.<T>builder()
                    .success(true)
                    .message(message)
                    .data(data)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        public static <T> ApiResponse<T> error(String message) {
            return ApiResponse.<T>builder()
                    .success(false)
                    .message(message)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private boolean success;
        private String message;
        private String error;

        @Builder.Default
        private LocalDateTime timestamp = LocalDateTime.now();

        public static ErrorResponse of(String message, String error) {
            return ErrorResponse.builder()
                    .success(false)
                    .message(message)
                    .error(error)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
}