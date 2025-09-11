package com.example.bankcards.dto.request;

import com.example.bankcards.utility.constant.RegExConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.YearMonth;
import org.hibernate.validator.constraints.UUID;

public record CardRequest(

    @Size(message = "{card.number.size.invalid}", min = 16, max = 19)
    @Pattern(message = "{card.number.pattern.invalid}", regexp = RegExConstant.cardNumberRegEx)
    @NotBlank(message = "{card.number.blank}")
    String cardNumber,

    @NotNull(message = "{card.expiry.date.null}")
    YearMonth expiryDate,

    @NotBlank(message = "{card.owner.id.empty}")
    @UUID(message = "{card.owner.id.invalid}")
    String ownerId,

    @NotNull(message = "{card.balance.null}")
    @PositiveOrZero(message = "{card.balance.negative}")
    BigDecimal balance

) {
}