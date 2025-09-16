package com.example.bankcards.dto.response;

import java.math.BigDecimal;

public record CardBalanceResponse(

    BigDecimal balance

) {
}
