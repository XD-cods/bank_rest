package com.example.bankcards.unit.service.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.constant.UnitTestDataProvider;
import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.impl.CardHashService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class CardHashServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardHashService cardHashService;

    private String cardNumber;

    @BeforeEach
    void setUp() {
        cardNumber = UnitTestDataProvider.TEST_CARD_NUMBER;
    }

    @Test
    void hashCardNumber_shouldReturnHashedValue() {
        when(passwordEncoder.encode(cardNumber)).thenReturn(UnitTestDataProvider.TEST_HASHED_CARD_NUMBER);

        String result = cardHashService.hashCardNumber(cardNumber);

        assertNotNull(result);
        assertEquals(UnitTestDataProvider.TEST_HASHED_CARD_NUMBER, result);
        verify(passwordEncoder).encode(cardNumber);
    }

    @Test
    void getQuickHash_shouldReturnConsistentHash() {
        String result1 = cardHashService.getQuickHash(cardNumber);
        String result2 = cardHashService.getQuickHash(cardNumber);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1, result2);
    }

    @Test
    void isCardNumberUnique_shouldReturnTrueForUniqueCard() {
        String quickHash = cardHashService.getQuickHash(cardNumber);
        Card existingCard = UnitTestDataProvider.cardWithHashes(UnitTestDataProvider.TEST_DIFFERENT_HASH, quickHash);

        when(cardRepository.findByQuickHash(quickHash)).thenReturn(List.of(existingCard));
        when(passwordEncoder.matches(cardNumber, existingCard.getCardNumberHash())).thenReturn(false);

        boolean result = cardHashService.isCardNumberUnique(cardNumber);

        assertTrue(result);
        verify(cardRepository).findByQuickHash(quickHash);
        verify(passwordEncoder).matches(cardNumber, existingCard.getCardNumberHash());
    }

    @Test
    void isCardNumberUnique_shouldReturnFalseForDuplicateCard() {
        String quickHash = cardHashService.getQuickHash(cardNumber);
        Card existingCard =
            UnitTestDataProvider.cardWithHashes(UnitTestDataProvider.TEST_HASHED_CARD_NUMBER, quickHash);

        when(cardRepository.findByQuickHash(quickHash)).thenReturn(List.of(existingCard));
        when(passwordEncoder.matches(cardNumber, existingCard.getCardNumberHash())).thenReturn(true);

        boolean result = cardHashService.isCardNumberUnique(cardNumber);

        assertFalse(result);
        verify(cardRepository).findByQuickHash(quickHash);
        verify(passwordEncoder).matches(cardNumber, existingCard.getCardNumberHash());
    }

    @Test
    void isCardNumberUnique_shouldReturnTrueWhenNoPotentialMatches() {
        String quickHash = cardHashService.getQuickHash(cardNumber);

        when(cardRepository.findByQuickHash(quickHash)).thenReturn(List.of());

        boolean result = cardHashService.isCardNumberUnique(cardNumber);

        assertTrue(result);
        verify(cardRepository).findByQuickHash(quickHash);
        verify(passwordEncoder, never()).matches(any(), any());
    }
}