package com.example.bankcards.unit.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.constant.UnitTestDataProvider;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.impl.TransferSecurityService;
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
public class TransferSecurityServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransferSecurityService transferSecurityService;

    private TransferRequest transferRequest;
    private UUID userId;
    private UUID sourceCardId;
    private UUID targetCardId;
    private Card targetCard;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        Card sourceCard = UnitTestDataProvider.sourceCard();
        targetCard = UnitTestDataProvider.targetCard();
        sourceCardId = sourceCard.getId();
        targetCardId = targetCard.getId();
        transferRequest = UnitTestDataProvider.transferRequest(sourceCard, targetCard);

        transferRequest = UnitTestDataProvider.transferRequest(sourceCard, targetCard);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canTransfer_shouldReturnFalseForUnauthenticatedUser() {
        boolean result = transferSecurityService.canTransfer(transferRequest);

        assertFalse(result);
        verify(cardRepository, never()).existsByIdAndOwnerId(any(), any());
    }

    @Test
    void canTransfer_shouldReturnFalseForInvalidUUIDFormat() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.authenticatedToken(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TransferRequest invalidRequest = UnitTestDataProvider.invalidSourceTransferRequest(targetCard);

        boolean result = transferSecurityService.canTransfer(invalidRequest);

        assertFalse(result);
        verify(cardRepository, never()).existsByIdAndOwnerId(any(), any());
    }

    @Test
    void canTransfer_shouldReturnFalseForNullAuthentication() {
        boolean result = transferSecurityService.canTransfer(transferRequest);

        assertFalse(result);
        verify(cardRepository, never()).existsByIdAndOwnerId(any(), any());
    }

    @Test
    void canTransfer_shouldReturnFalseForNotAuthenticated() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.unauthenticatedToken(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean result = transferSecurityService.canTransfer(transferRequest);

        assertFalse(result);
        verify(cardRepository, never()).existsByIdAndOwnerId(any(), any());
    }

    @Test
    void canTransfer_shouldReturnFalseForException() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.invalidUserIdToken();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean result = transferSecurityService.canTransfer(transferRequest);

        assertFalse(result);
        verify(cardRepository, never()).existsByIdAndOwnerId(any(), any());
    }

    @Test
    void canTransfer_shouldReturnTrueWhenBothCardsBelongToUser() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.authenticatedToken(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(cardRepository.existsByIdAndOwnerId(sourceCardId, userId)).thenReturn(true);
        when(cardRepository.existsByIdAndOwnerId(targetCardId, userId)).thenReturn(true);

        boolean result = transferSecurityService.canTransfer(transferRequest);

        assertTrue(result);
        verify(cardRepository).existsByIdAndOwnerId(sourceCardId, userId);
        verify(cardRepository).existsByIdAndOwnerId(targetCardId, userId);
    }

    @Test
    void canTransfer_shouldReturnFalseWhenOnlySourceCardBelongsToUser() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.authenticatedToken(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(cardRepository.existsByIdAndOwnerId(sourceCardId, userId)).thenReturn(true);
        when(cardRepository.existsByIdAndOwnerId(targetCardId, userId)).thenReturn(false);

        boolean result = transferSecurityService.canTransfer(transferRequest);

        assertFalse(result);
        verify(cardRepository).existsByIdAndOwnerId(sourceCardId, userId);
        verify(cardRepository).existsByIdAndOwnerId(targetCardId, userId);
    }

    @Test
    void canTransfer_shouldReturnFalseWhenOnlyTargetCardBelongsToUser() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.authenticatedToken(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(cardRepository.existsByIdAndOwnerId(sourceCardId, userId)).thenReturn(false);
        when(cardRepository.existsByIdAndOwnerId(targetCardId, userId)).thenReturn(true);

        boolean result = transferSecurityService.canTransfer(transferRequest);

        assertFalse(result);
        verify(cardRepository).existsByIdAndOwnerId(sourceCardId, userId);
        verify(cardRepository).existsByIdAndOwnerId(targetCardId, userId);
    }

    @Test
    void canTransfer_shouldReturnFalseWhenNoCardsBelongToUser() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.authenticatedToken(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(cardRepository.existsByIdAndOwnerId(sourceCardId, userId)).thenReturn(false);
        when(cardRepository.existsByIdAndOwnerId(targetCardId, userId)).thenReturn(false);

        boolean result = transferSecurityService.canTransfer(transferRequest);

        assertFalse(result);
        verify(cardRepository).existsByIdAndOwnerId(sourceCardId, userId);
        verify(cardRepository).existsByIdAndOwnerId(targetCardId, userId);
    }
}