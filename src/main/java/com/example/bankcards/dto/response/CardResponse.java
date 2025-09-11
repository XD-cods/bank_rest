package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardStatus;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

public record CardResponse(

    UUID id,

    String maskedCardNumber,

    YearMonth expiryDate,

    UUID ownerId,

    CardStatus cardStatus,

    BigDecimal balance

) {
}