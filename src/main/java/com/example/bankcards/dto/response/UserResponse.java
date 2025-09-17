package com.example.bankcards.dto.response;

import java.util.UUID;

public record UserResponse(

    UUID id,

    String firstName,

    String lastName,

    String email

) {
}