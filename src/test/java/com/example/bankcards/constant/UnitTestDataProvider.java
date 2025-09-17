package com.example.bankcards.constant;

import com.example.bankcards.dto.request.AuthRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.dto.response.CardBalanceResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnitTestDataProvider {

    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "password123";
    public static final String TEST_FIRST_NAME = "John";
    public static final String TEST_LAST_NAME = "Doe";
    public static final String TEST_CARD_NUMBER = "1234 5678 1234 5678";
    public static final BigDecimal TEST_BALANCE = new BigDecimal("1000.00");
    public static final String TEST_INVALID_UUID = "invalid-uuid";
    public static final String TEST_INVALID_USER_ID = "invalid-user-id-format";
    public static final BigDecimal TEST_TRANSFER_AMOUNT = new BigDecimal("200.00");
    public static final String TEST_TRANSFER_DESCRIPTION = "Test transfer";
    public static final BigDecimal TEST_INSUFFICIENT_BALANCE = new BigDecimal("100.00");
    public static final String TEST_UPDATE_FIRST_NAME = "Jane";
    public static final String TEST_UPDATE_LAST_NAME = "Smith";
    public static final String TEST_UPDATE_EMAIL = "jane.smith@example.com";
    public static final String TEST_UPDATE_PASSWORD = "newPassword123";
    public static final BigDecimal TEST_UPDATE_BALANCE = new BigDecimal("1500.00");
    public static final YearMonth TEST_UPDATE_EXPIRY_DATE = YearMonth.of(2026, 1);
    public static final String TEST_VALID_CARD_NUMBER = "1234567890123456";
    public static final String TEST_SHORT_CARD_NUMBER = "12345678901";
    public static final String TEST_CARD_NUMBER_WITH_SPACES = "1234 5678 9012 3456";
    public static final String TEST_MASKED_VALID = "1234 **** **** 3456";
    public static final String TEST_MASKED_WITH_SPACES = "1234 **** **** 3456";
    public static final String TEST_HASHED_PASSWORD = "encodedPassword";
    public static final String TEST_NEW_HASHED_PASSWORD = "newEncodedPassword";
    public static final String TEST_CARD_HASH = "hash";
    public static final String TEST_CARD_QUICK_HASH = "quickHash";
    public static final String TEST_MASKED_CARD_NUMBER = "1234 **** **** 5678";
    public static final YearMonth TEST_EXPIRY_DATE = YearMonth.of(2025, 12);
    public static final String TEST_JWT_TOKEN = "jwt-token";
    public static final String VALID_CARD_NUMBER = "1234567890123456";
    public static final String SHORT_CARD_NUMBER = "12345678901";
    public static final String CARD_NUMBER_WITH_SPACES = "1234 5678 9012 3456";
    public static final String MASKED_VALID = "1234 **** **** 3456";
    public static final String MASKED_WITH_SPACES = "1234 **** **** 3456";
    public static final String TEST_HASHED_CARD_NUMBER = "hashedCardNumber";
    public static final String TEST_DIFFERENT_HASH = "differentHash";

    public static User user() {
        return User.builder()
            .id(UUID.randomUUID())
            .firstName(TEST_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .email(TEST_EMAIL)
            .encryptedPassword(TEST_HASHED_PASSWORD)
            .role(Role.ROLE_USER)
            .isActive(true)
            .build();
    }

    public static User inactiveUser() {
        User user = user();
        user.setActive(false);
        return user;
    }

    public static UserResponse userResponse(User user) {
        return new UserResponse(
            user.getId(),
            TEST_FIRST_NAME,
            TEST_LAST_NAME,
            TEST_EMAIL
        );
    }

    public static UserRequest userRequest() {
        return new UserRequest(
            TEST_FIRST_NAME,
            TEST_LAST_NAME,
            TEST_EMAIL,
            TEST_PASSWORD
        );
    }

    public static Card cardWithHashes(String cardNumberHash, String quickHash) {
        User user = user();
        return Card.builder()
            .id(UUID.randomUUID())
            .cardNumberHash(cardNumberHash)
            .maskedCardNumber(TEST_MASKED_CARD_NUMBER)
            .quickHash(quickHash)
            .expiryDate(TEST_EXPIRY_DATE)
            .owner(user)
            .balance(BigDecimal.ZERO)
            .build();
    }

    public static UserRequest updateUserRequest() {
        return new UserRequest(
            TEST_UPDATE_FIRST_NAME,
            TEST_UPDATE_LAST_NAME,
            TEST_UPDATE_EMAIL,
            TEST_UPDATE_PASSWORD
        );
    }

    public static UserRequest updateUserRequestWithoutPassword() {
        return new UserRequest(
            TEST_UPDATE_FIRST_NAME,
            TEST_UPDATE_LAST_NAME,
            TEST_UPDATE_EMAIL,
            null
        );
    }

    public static UserResponse updatedUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            TEST_UPDATE_FIRST_NAME,
            TEST_UPDATE_LAST_NAME,
            TEST_UPDATE_EMAIL
        );
    }

    public static Card card(User user) {
        return Card.builder()
            .id(UUID.randomUUID())
            .cardNumberHash(TEST_CARD_HASH)
            .maskedCardNumber(TEST_MASKED_CARD_NUMBER)
            .quickHash(TEST_CARD_QUICK_HASH)
            .expiryDate(TEST_EXPIRY_DATE)
            .owner(user)
            .balance(TEST_BALANCE)
            .cardStatus(CardStatus.ACTIVE)
            .build();
    }

    public static Card blockedCard(User user) {
        Card card = card(user);
        card.setCardStatus(CardStatus.BLOCKED);
        return card;
    }

    public static Card sourceCard() {
        return Card.builder()
            .id(UUID.randomUUID())
            .balance(TEST_BALANCE)
            .build();
    }

    public static Card targetCard() {
        return Card.builder()
            .id(UUID.randomUUID())
            .balance(new BigDecimal("500.00"))
            .build();
    }

    public static Card sourceCardInsufficient() {
        return Card.builder()
            .id(UUID.randomUUID())
            .balance(TEST_INSUFFICIENT_BALANCE)
            .build();
    }

    public static CardResponse cardResponse(Card card, User user) {
        return new CardResponse(
            card.getId(),
            TEST_MASKED_CARD_NUMBER,
            TEST_EXPIRY_DATE,
            user.getId(),
            CardStatus.ACTIVE,
            TEST_BALANCE
        );
    }

    public static CardRequest cardRequest(User user) {
        return new CardRequest(
            TEST_CARD_NUMBER,
            TEST_EXPIRY_DATE,
            user.getId().toString(),
            TEST_BALANCE
        );
    }

    public static CardRequest updateCardRequest(User user) {
        return new CardRequest(
            TEST_CARD_NUMBER,
            TEST_UPDATE_EXPIRY_DATE,
            user.getId().toString(),
            TEST_UPDATE_BALANCE
        );
    }

    public static CardResponse updatedCardResponse(Card card, User user) {
        return new CardResponse(
            card.getId(),
            TEST_MASKED_CARD_NUMBER,
            TEST_UPDATE_EXPIRY_DATE,
            user.getId(),
            CardStatus.ACTIVE,
            TEST_UPDATE_BALANCE
        );
    }

    public static CardBalanceResponse cardBalanceResponse() {
        return new CardBalanceResponse(TEST_BALANCE);
    }

    public static AuthRequest authRequest() {
        return new AuthRequest(TEST_EMAIL, TEST_PASSWORD);
    }

    public static AuthResponse authResponse() {
        return new AuthResponse(TEST_JWT_TOKEN);
    }

    public static TransferRequest transferRequest(Card sourceCard, Card targetCard) {
        return new TransferRequest(
            sourceCard.getId().toString(),
            targetCard.getId().toString(),
            TEST_TRANSFER_AMOUNT,
            TEST_TRANSFER_DESCRIPTION
        );
    }

    public static TransferRequest transferRequestSameCard(Card card) {
        return new TransferRequest(
            card.getId().toString(),
            card.getId().toString(),
            TEST_TRANSFER_AMOUNT,
            TEST_TRANSFER_DESCRIPTION
        );
    }

    public static TransferRequest transferRequestInsufficient(Card sourceCard, Card targetCard) {
        return new TransferRequest(
            sourceCard.getId().toString(),
            targetCard.getId().toString(),
            TEST_TRANSFER_AMOUNT,
            TEST_TRANSFER_DESCRIPTION
        );
    }

    public static TransferRequest invalidSourceTransferRequest(Card targetCard) {
        return new TransferRequest(
            TEST_INVALID_UUID,
            targetCard.getId().toString(),
            new BigDecimal("100.00"),
            TEST_TRANSFER_DESCRIPTION
        );
    }

    public static UsernamePasswordAuthenticationToken authenticatedToken(UUID userId) {
        return new UsernamePasswordAuthenticationToken(
            userId.toString(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public static UsernamePasswordAuthenticationToken unauthenticatedToken(UUID userId) {
        return new UsernamePasswordAuthenticationToken(
            userId.toString(), null);
    }

    public static UsernamePasswordAuthenticationToken invalidUserIdToken() {
        return new UsernamePasswordAuthenticationToken(
            TEST_INVALID_USER_ID, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public static UsernamePasswordAuthenticationToken authenticationForLogin() {
        return new UsernamePasswordAuthenticationToken(
            TEST_EMAIL,
            TEST_PASSWORD,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}