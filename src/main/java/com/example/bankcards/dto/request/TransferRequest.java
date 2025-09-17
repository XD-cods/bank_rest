package com.example.bankcards.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.UUID;

public record TransferRequest(

    @UUID(message = "{transfer.request.source.card_id.not_valid}")
    @NotNull(message = "{transfer.request.source.card_id.null}")
    String sourceCardId,

    @UUID(message = "{transfer.request.target.card_id.not_valid}")
    @NotNull(message = "{transfer.request.target.card_id.null}")
    String targetCardId,

    @NotNull(message = "{transfer.request.amount.null}")
    @Positive(message = "{transfer.request.amount.negative}")
    @DecimalMin(value = "0.1", message = "{transfer.request.amount.least}")
    BigDecimal amount,

    String description

) {
}
