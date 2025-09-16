package com.example.bankcards.exception;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ErrorResponse(

    String error,

    String errorDescription,

    Integer statusCode,

    String path,

    LocalDateTime timestamp

) {
}