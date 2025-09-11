package com.example.bankcards.entity;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardStatus {
    ACTIVE(0), BLOCKED(1), EXPIRED(2);

    private final int code;

    public static CardStatus fromCode(int code) {
        return Arrays.stream(CardStatus.values()).filter(
                cardStatus -> cardStatus.getCode() == code
            )
            .findFirst()
            .orElse(BLOCKED);
    }
}
