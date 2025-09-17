package com.example.bankcards.unit.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.constant.UnitTestDataProvider;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.impl.CardSecurityService;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class CardSecurityServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardSecurityService cardSecurityService;

    private UUID cardId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isCardOwner_shouldReturnTrueForCardOwner() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.authenticatedToken(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(cardRepository.existsByIdAndOwnerId(cardId, userId)).thenReturn(true);

        boolean result = cardSecurityService.isCardOwner(cardId);

        assertTrue(result);
        verify(cardRepository).existsByIdAndOwnerId(cardId, userId);
    }

    @Test
    void isCardOwner_shouldReturnFalseForNonOwner() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.authenticatedToken(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(cardRepository.existsByIdAndOwnerId(cardId, userId)).thenReturn(false);

        boolean result = cardSecurityService.isCardOwner(cardId);

        assertFalse(result);
        verify(cardRepository).existsByIdAndOwnerId(cardId, userId);
    }

    @Test
    void isCardOwner_shouldReturnFalseForUnauthenticatedUser() {
        boolean result = cardSecurityService.isCardOwner(cardId);

        assertFalse(result);
        verify(cardRepository, never()).existsByIdAndOwnerId(any(), any());
    }

    @Test
    void isCardOwner_shouldReturnFalseForNotAuthenticated() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.unauthenticatedToken(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean result = cardSecurityService.isCardOwner(cardId);

        assertFalse(result);
        verify(cardRepository, never()).existsByIdAndOwnerId(any(), any());
    }

    @Test
    void isCardOwner_shouldReturnFalseForInvalidUserIdFormat() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.invalidUserIdToken();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean result = cardSecurityService.isCardOwner(cardId);

        assertFalse(result);
        verify(cardRepository, never()).existsByIdAndOwnerId(any(), any());
    }
}