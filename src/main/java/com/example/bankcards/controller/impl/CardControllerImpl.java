package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.CardController;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardBalanceResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.PageResponse;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardControllerImpl implements CardController {

    private final CardService cardService;

    @GetMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN') or @cardSecurityService.isCardOwner(#cardId)")
    public CardResponse getCardDetails(@PathVariable UUID cardId) {
        CardResponse cardDetails = cardService.getCardDetails(cardId);
        return cardDetails;
    }

    @GetMapping("/{cardId}/balance")
    @PreAuthorize("hasRole('ADMIN') or @cardSecurityService.isCardOwner(#cardId)")
    public CardBalanceResponse getCardBalance(@PathVariable UUID cardId) {
        CardBalanceResponse cardBalanceResponse = cardService.getCardBalanceResponse(cardId);
        return cardBalanceResponse;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public PageResponse<CardResponse> getUserCards(
        @PathVariable UUID userId,
        @RequestParam(required = false, defaultValue = "0") @Min(0) Integer currentPage,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer limit,
        @RequestParam(required = false) String search
    ) {
        PageResponse<CardResponse> userCards = cardService.getUserCards(userId, currentPage, limit, search);
        return userCards;
    }

    @GetMapping("/user/me")
    @PreAuthorize("isAuthenticated()")
    public PageResponse<CardResponse> getCurrentUserCards(
        @RequestParam(required = false, defaultValue = "0") @Min(0) Integer currentPage,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer limit,
        @RequestParam(required = false) String search,
        Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());
        PageResponse<CardResponse> userCards = cardService.getUserCards(userId, currentPage, limit, search);
        return userCards;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<CardResponse> getAllCards(
        @RequestParam(required = false, defaultValue = "0") @Min(0) Integer currentPage,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer limit,
        @RequestParam(required = false) String search
    ) {
        PageResponse<CardResponse> cards = cardService.getAllCards(currentPage, limit, search);
        return cards;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CardResponse createCard(@Valid @RequestBody CardRequest cardRequest) {
        CardResponse cardResponse = cardService.createNewCard(cardRequest);
        return cardResponse;
    }

    @PutMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CardResponse updateCardDetails(
        @PathVariable UUID cardId,
        @Valid @RequestBody CardRequest updateCardRequest
    ) {
        CardResponse cardResponse = cardService.updateCardDetails(cardId, updateCardRequest);
        return cardResponse;
    }

    @PatchMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@transferSecurityService.canTransfer(#transferRequest)")
    public void transferMoney(@Valid @RequestBody TransferRequest transferRequest) {
        cardService.transferMoneyByTransferRequest(transferRequest);
    }

    @PatchMapping("/{cardId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @cardSecurityService.isCardOwner(#cardId)")
    public void blockCard(@PathVariable UUID cardId) {
        cardService.blockCardByCardId(cardId);
    }

    @PatchMapping("/{cardId}/unblock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void unblockCard(@PathVariable UUID cardId) {
        cardService.unlockCardByCardId(cardId);
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCard(@PathVariable UUID cardId) {
        cardService.deleteCard(cardId);
    }

}
