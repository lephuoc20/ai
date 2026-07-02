package com.phuoclv.ai.api.conf;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        long timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "Operation completed successfully", Instant.now().toEpochMilli());
    }
}