package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardBalanceResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface CardController {

    CardResponse getCardDetails(@PathVariable UUID cardId);

    CardBalanceResponse getCardBalance(@PathVariable UUID cardId);

    void transferMoney(@Valid @RequestBody TransferRequest transferRequest);

    CardResponse createCard(@Valid @RequestBody CardRequest cardRequest);

    PageResponse<CardResponse> getUserCards(
        @PathVariable UUID userId,
        @RequestParam(required = false, defaultValue = "0") @Min(0) Integer currentPage,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer limit,
        @RequestParam(required = false) String search
    );

    CardResponse updateCardDetails(
        @PathVariable UUID cardId,
        @Valid @RequestBody CardRequest cardRequest
    );

    void blockCard(@PathVariable UUID cardId);

    void unblockCard(@PathVariable UUID cardId);

    void deleteCard(@PathVariable UUID cardId);

    PageResponse<CardResponse> getAllCards(
        @RequestParam(required = false, defaultValue = "0") @Min(0) Integer currentPage,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer limit,
        @RequestParam(required = false) String search
    );

}
