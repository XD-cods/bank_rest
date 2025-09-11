package com.example.bankcards.entity;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_USER(0),
    ROLE_ADMIN(1);

    private final int code;

    public static Role fromCode(int code) {
        return Arrays.stream(Role.values())
            .filter(role -> role.getCode() == code)
            .findFirst()
            .orElse(ROLE_USER);
    }

}
