package com.example.bankcards.service;

import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardBalanceResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;

public interface CardService {

    CardResponse getCardDetails(UUID cardId);

    CardBalanceResponse getCardBalanceResponse(UUID cardId);

    PageResponse<CardResponse> getUserCards(
        UUID userId, @Min(0) Integer currentPage, @Min(1) @Max(100) Integer limit, String search
    );

    PageResponse<CardResponse> getAllCards(@Min(0) Integer currentPage, @Min(1) @Max(100) Integer limit, String search);

    CardResponse createNewCard(@Valid CardRequest cardRequest);

    CardResponse updateCardDetails(UUID cardId, @Valid CardRequest updateCardRequest);

    void transferMoneyByTransferRequest(@Valid TransferRequest transferRequest);

    void blockCardByCardId(UUID cardId);

    void unlockCardByCardId(UUID cardId);

    void deleteCard(UUID cardId);

}
