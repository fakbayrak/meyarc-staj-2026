package com.meyarc.library.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ErrorResponse {

    private final Instant timestamp = Instant.now();
    private final int status;
    private final String message;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
