package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(

    @NotBlank(message = "{auth.email.blank}")
    String email,

    @NotBlank(message = "{auth.password.blank}")
    String password

) {
}
