package com.example.bankcards.dto.response;

import java.util.List;
import java.util.UUID;

public record UserResponse(

    UUID id,

    String firstName,

    List<UUID> cardIds,

    boolean isActive

) {
}