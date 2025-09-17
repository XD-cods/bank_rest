package com.example.bankcards.unit.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.bankcards.constant.UnitTestDataProvider;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardBalanceResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.PageResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.error.BadRequestException;
import com.example.bankcards.exception.error.CardAlreadyExistsByCardNumberException;
import com.example.bankcards.exception.error.CardNotFoundById;
import com.example.bankcards.exception.error.UserNotFoundById;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.CardHashService;
import com.example.bankcards.service.impl.CardServiceImpl;
import com.example.bankcards.utility.constant.ErrorMessagesConstant;
import com.example.bankcards.utility.mapper.CardMapper;
import com.example.bankcards.utility.mapper.PageResponseMapper;
import com.example.bankcards.utility.validator.CardValidator;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardHashService cardHashService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @Mock
    private CardValidator cardValidator;

    @InjectMocks
    private CardServiceImpl cardService;

    private Card card;
    private CardResponse cardResponse;
    private CardRequest cardRequest;
    private User user;
    private UUID cardId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        user = UnitTestDataProvider.user();
        card = UnitTestDataProvider.card(user);
        cardResponse = UnitTestDataProvider.cardResponse(card, user);
        cardRequest = UnitTestDataProvider.cardRequest(user);
        cardId = card.getId();
        userId = user.getId();
    }

    @Test
    void getCardDetails_shouldReturnCardResponse() {
        when(cardRepository.findCardById(cardId)).thenReturn(Optional.of(card));
        when(cardMapper.toResponse(card)).thenReturn(cardResponse);

        CardResponse result = cardService.getCardDetails(cardId);

        assertNotNull(result);
        assertEquals(cardResponse, result);

        verify(cardRepository).findCardById(cardId);
        verify(cardMapper).toResponse(card);
    }

    @Test
    void getCardDetails_shouldThrowCardNotFoundException() {
        when(cardRepository.findCardById(cardId)).thenReturn(Optional.empty());

        CardNotFoundById exception = assertThrows(
            CardNotFoundById.class,
            () -> cardService.getCardDetails(cardId)
        );

        assertEquals(
            ErrorMessagesConstant.CARD_NOT_FOUND_BY_ID_MESSAGE.formatted(cardId),
            exception.getMessage()
        );

        verify(cardRepository).findCardById(cardId);
        verifyNoInteractions(cardMapper);
    }

    @Test
    void getCardBalanceResponse_shouldReturnBalance() {
        when(cardRepository.findCardById(cardId)).thenReturn(Optional.of(card));

        CardBalanceResponse result = cardService.getCardBalanceResponse(cardId);

        assertNotNull(result);
        assertEquals(UnitTestDataProvider.cardBalanceResponse().balance(), result.balance());

        verify(cardRepository).findCardById(cardId);
    }

    @Test
    void getCardBalanceResponse_shouldThrowCardNotFoundException() {
        when(cardRepository.findCardById(cardId)).thenReturn(Optional.empty());

        CardNotFoundById exception = assertThrows(
            CardNotFoundById.class,
            () -> cardService.getCardBalanceResponse(cardId)
        );

        assertEquals(
            ErrorMessagesConstant.CARD_NOT_FOUND_BY_ID_MESSAGE.formatted(cardId),
            exception.getMessage()
        );

        verify(cardRepository).findCardById(cardId);
    }

    @Test
    void getUserCards_shouldReturnPageResponse() {
        int currentPage = 0;
        int limit = 10;
        PageRequest pageRequest = PageRequest.of(currentPage, limit);
        List<Card> cards = List.of(card);
        Page<Card> cardPage = new PageImpl<>(cards, pageRequest, cards.size());

        when(cardRepository.findByOwnerId(userId, pageRequest)).thenReturn(cardPage);
        when(pageResponseMapper.toPageResponse(eq(cardPage), eq(currentPage), any()))
            .thenReturn(new PageResponse<>(List.of(cardResponse), currentPage, cardPage.getTotalElements(),
                cardPage.getTotalPages()));

        PageResponse<CardResponse> result = cardService.getUserCards(userId, currentPage, limit, "");

        assertNotNull(result);
        assertEquals(cards.size(), result.content().size());
        assertEquals(currentPage, result.currentPage());

        verify(cardRepository).findByOwnerId(userId, pageRequest);
        verify(pageResponseMapper).toPageResponse(eq(cardPage), eq(currentPage), any());
    }

    @Test
    void getAllCards_shouldReturnPageResponse() {
        int currentPage = 0;
        int limit = 10;
        PageRequest pageRequest = PageRequest.of(currentPage, limit);
        List<Card> cards = List.of(card);
        Page<Card> cardPage = new PageImpl<>(cards, pageRequest, cards.size());

        when(cardRepository.findAll(pageRequest)).thenReturn(cardPage);
        when(pageResponseMapper.toPageResponse(eq(cardPage), eq(currentPage), any()))
            .thenReturn(new PageResponse<>(List.of(cardResponse), currentPage, cardPage.getTotalElements(),
                cardPage.getTotalPages()));

        PageResponse<CardResponse> result = cardService.getAllCards(currentPage, limit, "");

        assertNotNull(result);
        assertEquals(cards.size(), result.content().size());
        assertEquals(currentPage, result.currentPage());

        verify(cardRepository).findAll(pageRequest);
        verify(pageResponseMapper).toPageResponse(eq(cardPage), eq(currentPage), any());
    }

    @Test
    void createNewCard_shouldCreateCardSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardHashService.isCardNumberUnique(cardRequest.cardNumber())).thenReturn(true);
        when(cardHashService.hashCardNumber(cardRequest.cardNumber())).thenReturn(
            UnitTestDataProvider.TEST_CARD_HASH);
        when(cardHashService.getQuickHash(cardRequest.cardNumber())).thenReturn(
            UnitTestDataProvider.TEST_CARD_QUICK_HASH);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toResponse(card)).thenReturn(cardResponse);

        CardResponse result = cardService.createNewCard(cardRequest);

        assertNotNull(result);
        assertEquals(cardResponse, result);

        verify(userRepository).findById(userId);
        verify(cardHashService).isCardNumberUnique(cardRequest.cardNumber());
        verify(cardHashService).hashCardNumber(cardRequest.cardNumber());
        verify(cardHashService).getQuickHash(cardRequest.cardNumber());
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toResponse(card);
    }

    @Test
    void createNewCard_shouldThrowUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundById exception = assertThrows(
            UserNotFoundById.class,
            () -> cardService.createNewCard(cardRequest)
        );

        assertEquals(
            ErrorMessagesConstant.USER_NOT_FOUND_BY_ID_MESSAGE.formatted(userId),
            exception.getMessage()
        );

        verify(userRepository).findById(userId);
        verifyNoInteractions(cardHashService, cardRepository, cardMapper);
    }

    @Test
    void createNewCard_shouldThrowCardAlreadyExistsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardHashService.isCardNumberUnique(cardRequest.cardNumber())).thenReturn(false);

        CardAlreadyExistsByCardNumberException exception = assertThrows(
            CardAlreadyExistsByCardNumberException.class,
            () -> cardService.createNewCard(cardRequest)
        );

        assertEquals(ErrorMessagesConstant.CARD_ALREADY_EXISTS_BY_CARD_NUMBER, exception.getMessage());

        verify(userRepository).findById(userId);
        verify(cardHashService).isCardNumberUnique(cardRequest.cardNumber());
        verifyNoInteractions(cardRepository, cardMapper);
        verify(cardHashService, never()).hashCardNumber(any());
        verify(cardHashService, never()).getQuickHash(any());
    }

    @Test
    void updateCardDetails_shouldUpdateCardSuccessfully() {
        CardRequest updateRequest = UnitTestDataProvider.updateCardRequest(user);
        CardResponse updatedResponse = UnitTestDataProvider.updatedCardResponse(card, user);

        when(cardRepository.findCardById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toResponse(card)).thenReturn(updatedResponse);

        CardResponse result = cardService.updateCardDetails(cardId, updateRequest);

        assertNotNull(result);
        assertEquals(updatedResponse, result);

        verify(cardRepository).findCardById(cardId);
        verify(cardValidator).validateCardForUpade(card);
        verify(cardRepository).save(card);
        verify(cardMapper).toResponse(card);
    }

    @Test
    void updateCardDetails_shouldThrowCardNotFoundException() {
        CardRequest updateRequest = UnitTestDataProvider.updateCardRequest(user);

        when(cardRepository.findCardById(cardId)).thenReturn(Optional.empty());

        CardNotFoundById exception = assertThrows(
            CardNotFoundById.class,
            () -> cardService.updateCardDetails(cardId, updateRequest)
        );

        assertEquals(
            ErrorMessagesConstant.CARD_NOT_FOUND_BY_ID_MESSAGE.formatted(cardId),
            exception.getMessage()
        );

        verify(cardRepository).findCardById(cardId);
        verifyNoInteractions(cardValidator, cardMapper);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void transferMoneyByTransferRequest_shouldTransferSuccessfully() {
        Card sourceCard = UnitTestDataProvider.sourceCard();
        Card targetCard = UnitTestDataProvider.targetCard();
        TransferRequest transferRequest = UnitTestDataProvider.transferRequest(sourceCard, targetCard);

        when(cardRepository.findCardById(sourceCard.getId())).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findCardById(targetCard.getId())).thenReturn(Optional.of(targetCard));
        when(cardRepository.save(sourceCard)).thenReturn(sourceCard);
        when(cardRepository.save(targetCard)).thenReturn(targetCard);

        cardService.transferMoneyByTransferRequest(transferRequest);

        assertEquals(UnitTestDataProvider.TEST_BALANCE.subtract(UnitTestDataProvider.TEST_TRANSFER_AMOUNT),
            sourceCard.getBalance());
        assertEquals(new BigDecimal("500.00").add(UnitTestDataProvider.TEST_TRANSFER_AMOUNT),
            targetCard.getBalance());

        verify(cardRepository).findCardById(sourceCard.getId());
        verify(cardRepository).findCardById(targetCard.getId());
        verify(cardValidator).validateCardsForTransfer(sourceCard, targetCard);
        verify(cardRepository).save(sourceCard);
        verify(cardRepository).save(targetCard);
    }

    @Test
    void transferMoneyByTransferRequest_shouldThrowSameCardException() {
        TransferRequest transferRequest = UnitTestDataProvider.transferRequestSameCard(card);

        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> cardService.transferMoneyByTransferRequest(transferRequest)
        );

        assertEquals(ErrorMessagesConstant.TRANSFER_SAME_CARD, exception.getMessage());

        verifyNoInteractions(cardRepository, cardValidator);
    }

    @Test
    void transferMoneyByTransferRequest_shouldThrowInsufficientBalanceException() {
        Card sourceCard = UnitTestDataProvider.sourceCardInsufficient();
        Card targetCard = UnitTestDataProvider.targetCard();
        TransferRequest transferRequest = UnitTestDataProvider.transferRequestInsufficient(sourceCard, targetCard);

        when(cardRepository.findCardById(sourceCard.getId())).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findCardById(targetCard.getId())).thenReturn(Optional.of(targetCard));

        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> cardService.transferMoneyByTransferRequest(transferRequest)
        );

        assertEquals(ErrorMessagesConstant.INSUFFICIENT_BALANCE, exception.getMessage());

        verify(cardRepository).findCardById(sourceCard.getId());
        verify(cardRepository).findCardById(targetCard.getId());
        verify(cardValidator).validateCardsForTransfer(sourceCard, targetCard);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void transferMoneyByTransferRequest_shouldThrowCardNotFoundExceptionForSourceCard() {
        Card targetCard = UnitTestDataProvider.targetCard();
        TransferRequest transferRequest =
            UnitTestDataProvider.transferRequest(UnitTestDataProvider.sourceCard(), targetCard);

        when(cardRepository.findCardById(UUID.fromString(transferRequest.sourceCardId()))).thenReturn(
            Optional.empty());

        CardNotFoundById exception = assertThrows(
            CardNotFoundById.class,
            () -> cardService.transferMoneyByTransferRequest(transferRequest)
        );

        assertEquals(
            ErrorMessagesConstant.CARD_NOT_FOUND_BY_ID_MESSAGE.formatted(transferRequest.sourceCardId()),
            exception.getMessage()
        );

        verify(cardRepository).findCardById(UUID.fromString(transferRequest.sourceCardId()));
        verify(cardRepository, never()).findCardById(UUID.fromString(transferRequest.targetCardId()));
        verifyNoInteractions(cardValidator);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void transferMoneyByTransferRequest_shouldThrowCardNotFoundExceptionForTargetCard() {
        Card sourceCard = UnitTestDataProvider.sourceCard();
        TransferRequest transferRequest =
            UnitTestDataProvider.transferRequest(sourceCard, UnitTestDataProvider.targetCard());

        when(cardRepository.findCardById(UUID.fromString(transferRequest.sourceCardId()))).thenReturn(
            Optional.of(sourceCard));
        when(cardRepository.findCardById(UUID.fromString(transferRequest.targetCardId()))).thenReturn(
            Optional.empty());

        CardNotFoundById exception = assertThrows(
            CardNotFoundById.class,
            () -> cardService.transferMoneyByTransferRequest(transferRequest)
        );

        assertEquals(
            ErrorMessagesConstant.CARD_NOT_FOUND_BY_ID_MESSAGE.formatted(transferRequest.targetCardId()),
            exception.getMessage()
        );

        verify(cardRepository).findCardById(UUID.fromString(transferRequest.sourceCardId()));
        verify(cardRepository).findCardById(UUID.fromString(transferRequest.targetCardId()));
        verifyNoInteractions(cardValidator);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void blockCardByCardId_shouldBlockCardSuccessfully() {
        when(cardRepository.findCardById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        cardService.blockCardByCardId(cardId);

        assertEquals(CardStatus.BLOCKED, card.getCardStatus());

        verify(cardRepository).findCardById(cardId);
        verify(cardValidator).validateCardForBlock(card);
        verify(cardRepository).save(card);
    }

    @Test
    void blockCardByCardId_shouldThrowCardNotFoundException() {
        when(cardRepository.findCardById(cardId)).thenReturn(Optional.empty());

        CardNotFoundById exception = assertThrows(
            CardNotFoundById.class,
            () -> cardService.blockCardByCardId(cardId)
        );

        assertEquals(
            ErrorMessagesConstant.CARD_NOT_FOUND_BY_ID_MESSAGE.formatted(cardId),
            exception.getMessage()
        );

        verify(cardRepository).findCardById(cardId);
        verifyNoInteractions(cardValidator);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void unlockCardByCardId_shouldUnlockCardSuccessfully() {
        Card blockedCard = UnitTestDataProvider.blockedCard(user);
        when(cardRepository.findCardById(blockedCard.getId())).thenReturn(Optional.of(blockedCard));
        when(cardRepository.save(blockedCard)).thenReturn(blockedCard);

        cardService.unlockCardByCardId(blockedCard.getId());

        assertEquals(CardStatus.ACTIVE, blockedCard.getCardStatus());

        verify(cardRepository).findCardById(blockedCard.getId());
        verify(cardValidator).validateCardForUnlock(blockedCard);
        verify(cardRepository).save(blockedCard);
    }

    @Test
    void unlockCardByCardId_shouldThrowCardNotFoundException() {
        when(cardRepository.findCardById(cardId)).thenReturn(Optional.empty());

        CardNotFoundById exception = assertThrows(
            CardNotFoundById.class,
            () -> cardService.unlockCardByCardId(cardId)
        );

        assertEquals(
            ErrorMessagesConstant.CARD_NOT_FOUND_BY_ID_MESSAGE.formatted(cardId),
            exception.getMessage()
        );

        verify(cardRepository).findCardById(cardId);
        verifyNoInteractions(cardValidator);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void deleteCard_shouldDeleteCardSuccessfully() {
        when(cardRepository.findCardById(cardId)).thenReturn(Optional.of(card));

        cardService.deleteCard(cardId);

        verify(cardRepository).findCardById(cardId);
        verify(cardValidator).validateCardForDelete(card);
        verify(cardRepository).delete(card);
    }

    @Test
    void deleteCard_shouldThrowCardNotFoundException() {
        when(cardRepository.findCardById(cardId)).thenReturn(Optional.empty());

        CardNotFoundById exception = assertThrows(
            CardNotFoundById.class,
            () -> cardService.deleteCard(cardId)
        );

        assertEquals(
            ErrorMessagesConstant.CARD_NOT_FOUND_BY_ID_MESSAGE.formatted(cardId),
            exception.getMessage()
        );

        verify(cardRepository).findCardById(cardId);
        verifyNoInteractions(cardValidator);
        verify(cardRepository, never()).delete(any(Card.class));
    }

    @Test
    void maskCardNumber_shouldMaskValidCardNumber() throws Exception {
        Method maskCardNumberMethod = CardServiceImpl.class.getDeclaredMethod("maskCardNumber", String.class);
        maskCardNumberMethod.setAccessible(true);

        String result = (String) maskCardNumberMethod.invoke(cardService, UnitTestDataProvider.VALID_CARD_NUMBER);

        assertEquals(UnitTestDataProvider.MASKED_VALID, result);
    }

    @Test
    void maskCardNumber_shouldReturnShortCardNumberUnchanged() throws Exception {
        Method maskCardNumberMethod = CardServiceImpl.class.getDeclaredMethod("maskCardNumber", String.class);
        maskCardNumberMethod.setAccessible(true);

        String result = (String) maskCardNumberMethod.invoke(cardService, UnitTestDataProvider.SHORT_CARD_NUMBER);

        assertEquals(UnitTestDataProvider.SHORT_CARD_NUMBER, result);
    }

    @Test
    void maskCardNumber_shouldReturnNullCardNumber() throws Exception {
        Method maskCardNumberMethod = CardServiceImpl.class.getDeclaredMethod("maskCardNumber", String.class);
        maskCardNumberMethod.setAccessible(true);

        String result = (String) maskCardNumberMethod.invoke(cardService, (String) null);

        assertNull(result);
    }

    @Test
    void maskCardNumber_shouldMaskCardNumberWithSpaces() throws Exception {
        Method maskCardNumberMethod = CardServiceImpl.class.getDeclaredMethod("maskCardNumber", String.class);
        maskCardNumberMethod.setAccessible(true);

        String result = (String) maskCardNumberMethod.invoke(cardService, UnitTestDataProvider.CARD_NUMBER_WITH_SPACES);

        assertEquals(UnitTestDataProvider.MASKED_WITH_SPACES, result);
    }
}