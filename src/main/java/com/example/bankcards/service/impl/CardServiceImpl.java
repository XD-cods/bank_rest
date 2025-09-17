package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardBalanceResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.PageResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.error.BadRequestException;
import com.example.bankcards.exception.error.CardAlreadyExistsByCardNumberException;
import com.example.bankcards.exception.error.CardNotFoundById;
import com.example.bankcards.exception.error.UserNotFoundById;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.utility.constant.ErrorMessagesConstant;
import com.example.bankcards.utility.mapper.CardMapper;
import com.example.bankcards.utility.mapper.PageResponseMapper;
import com.example.bankcards.utility.validator.CardValidator;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardHashService cardHashService;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final PageResponseMapper pageResponseMapper;
    private final CardValidator cardValidator;

    public CardResponse getCardDetails(UUID cardId) {
        log.debug("getCardDetails. Entering method. Card id: {}", cardId);
        Card card = getCardByCardId(cardId);

        log.info("getCardDetails. Card details founded. Card id: {}", cardId);
        return cardMapper.toResponse(card);
    }

    public CardBalanceResponse getCardBalanceResponse(UUID cardId) {
        log.debug("getCardBalanceResponse. Entering method. Card id: {}", cardId);

        Card card = getCardByCardId(cardId);
        BigDecimal cardBalance = card.getBalance();

        log.info("getCardBalanceResponse. Card balance founded. Card id: {}", cardId);
        return new CardBalanceResponse(cardBalance);
    }

    public PageResponse<CardResponse> getUserCards(UUID userId, Integer currentPage, Integer limit, String search) {
        log.debug(
            "getUserCards. Entering method. User id: {}, current page: {}, limit: {}, search: {}",
            userId, currentPage, limit, search
        );
        Pageable pageable = PageRequest.of(currentPage, limit);
        Page<Card> pageOfCard = cardRepository.findByOwnerId(userId, pageable);

        PageResponse<CardResponse> pageResponseOfCard = pageResponseMapper.toPageResponse(
            pageOfCard,
            currentPage,
            cardMapper::toResponse
        );

        log.info("getUserCards. Page of card by user id retrieved. User id: {}", userId);
        return pageResponseOfCard;
    }

    public PageResponse<CardResponse> getAllCards(Integer currentPage, Integer limit, String search) {
        log.debug(
            "getAllCards. Entering method. Current page: {}, limit: {}, search: {}",
            currentPage, limit, search
        );

        Pageable pageable = PageRequest.of(currentPage, limit);
        Page<Card> pageOfCard = cardRepository.findAll(pageable);

        PageResponse<CardResponse> pageResponseOfCard = pageResponseMapper.toPageResponse(
            pageOfCard,
            currentPage,
            cardMapper::toResponse
        );

        log.info("getAllCards. All page of cards retrieved.");
        return pageResponseOfCard;
    }

    @Transactional
    public CardResponse createNewCard(CardRequest cardRequest) {
        log.debug("createNewCard. Entering method. Card request: {}", cardRequest);

        UUID ownerId = UUID.fromString(cardRequest.ownerId());
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> {
                log.error("createNewCard. User not found with id: {}", ownerId);
                return new UserNotFoundById(ErrorMessagesConstant.USER_NOT_FOUND_BY_ID_MESSAGE.formatted(ownerId));
            });

        String cardNumber = cardRequest.cardNumber();

        if (!cardHashService.isCardNumberUnique(cardNumber)) {
            log.warn("createNewCard. Card already exists for user: {}", ownerId);
            throw new CardAlreadyExistsByCardNumberException(
                ErrorMessagesConstant.CARD_ALREADY_EXISTS_BY_CARD_NUMBER);
        }
        String maskedCardNumber = maskCardNumber(cardNumber);
        String cardNumberHash = cardHashService.hashCardNumber(cardNumber);
        String quickHash = cardHashService.getQuickHash(cardNumber);

        Card card = Card.builder()
            .cardNumberHash(cardNumberHash)
            .maskedCardNumber(maskedCardNumber)
            .quickHash(quickHash)
            .expiryDate(cardRequest.expiryDate())
            .owner(owner)
            .balance(cardRequest.balance())
            .build();

        owner.addCard(card);
        Card savedCard = cardRepository.save(card);

        log.info("createNewCard. Card created successfully. Card id: {}", savedCard.getId());
        return cardMapper.toResponse(savedCard);
    }

    @Transactional
    public CardResponse updateCardDetails(UUID cardId, CardRequest updateCardRequest) {
        log.debug("updateCardDetails. Entering method. Card id: {}, request: {}", cardId, updateCardRequest);

        Card card = getCardByCardId(cardId);

        cardValidator.validateCardForUpade(card);

        card.setExpiryDate(updateCardRequest.expiryDate());
        card.setBalance(updateCardRequest.balance());

        Card updatedCard = cardRepository.save(card);
        log.info("updateCardDetails. Card updated successfully. Card id: {}", cardId);
        return cardMapper.toResponse(updatedCard);
    }

    @Transactional
    public void transferMoneyByTransferRequest(TransferRequest transferRequest) {
        log.debug("transferMoneyByTransferRequest. Entering method. Transfer request: {}", transferRequest);

        UUID sourceCardId = UUID.fromString(transferRequest.sourceCardId());
        UUID targetCardId = UUID.fromString(transferRequest.targetCardId());

        if (sourceCardId.equals(targetCardId)) {
            log.error("transferMoneyByTransferRequest. Cannot transfer to same card: {}", sourceCardId);
            throw new BadRequestException(ErrorMessagesConstant.TRANSFER_SAME_CARD);
        }

        Card sourceCard = getCardByCardId(sourceCardId);
        Card targetCard = getCardByCardId(targetCardId);

        cardValidator.validateCardsForTransfer(sourceCard, targetCard);

        BigDecimal amount = transferRequest.amount();
        if (sourceCard.getBalance().compareTo(amount) < 0) {
            log.error("transferMoneyByTransferRequest. Transfer failed, insufficient balance : {}", sourceCardId);
            throw new BadRequestException(ErrorMessagesConstant.INSUFFICIENT_BALANCE);
        }

        sourceCard.setBalance(sourceCard.getBalance().subtract(amount));
        targetCard.setBalance(targetCard.getBalance().add(amount));

        cardRepository.save(sourceCard);
        cardRepository.save(targetCard);

        log.info("transferMoneyByTransferRequest. Transfer completed successfully. From: {}, To: {}, Amount: {}",
            sourceCardId, targetCardId, amount);
    }

    @Transactional
    public void blockCardByCardId(UUID cardId) {
        log.debug("blockCardByCardId. Entering method. Card id: {}", cardId);

        Card card = getCardByCardId(cardId);

        cardValidator.validateCardForBlock(card);

        card.block();
        cardRepository.save(card);
        log.info("blockCardByCardId. Card blocked successfully. Card id: {}", cardId);
    }

    @Transactional
    public void unlockCardByCardId(UUID cardId) {
        log.debug("unlockCardByCardId. Entering method. Card id: {}", cardId);

        Card card = getCardByCardId(cardId);

        cardValidator.validateCardForUnlock(card);

        card.activate();
        cardRepository.save(card);
        log.info("unlockCardByCardId. Card unlocked successfully. Card id: {}", cardId);
    }

    @Transactional
    public void deleteCard(UUID cardId) {
        log.debug("deleteCard. Entering method. Card id: {}", cardId);

        Card card = getCardByCardId(cardId);

        cardValidator.validateCardForDelete(card);

        cardRepository.delete(card);
        log.info("deleteCard. Card deleted successfully. Card id: {}", cardId);
    }

    private Card getCardByCardId(UUID cardId) {
        log.debug("getCardByCardId. Search card by card Id: {}", cardId);

        return cardRepository.findCardById(cardId)
            .orElseThrow(() -> {
                log.error("getCardByCardId. Card not found with id {}", cardId);
                return new CardNotFoundById(ErrorMessagesConstant.CARD_NOT_FOUND_BY_ID_MESSAGE.formatted(cardId));
            });
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 12) {
            return cardNumber;
        }

        String cleanNumber = cardNumber.replaceAll("[^0-9]", "");

        if (cleanNumber.length() < 12) {
            return cardNumber;
        }

        String firstFour = cleanNumber.substring(0, 4);
        String lastFour = cleanNumber.substring(cleanNumber.length() - 4);

        String maskedMiddle = "**** ****";

        return firstFour + " " + maskedMiddle + " " + lastFour;
    }

}
