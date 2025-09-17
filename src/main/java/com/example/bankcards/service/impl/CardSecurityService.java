package com.example.bankcards.service.impl;

import com.example.bankcards.repository.CardRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardSecurityService {

    private final CardRepository cardRepository;

    public boolean isCardOwner(UUID cardId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthenticated access attempt to card: {}", cardId);
                return false;
            }

            String currentUserId = authentication.getName();
            UUID userId = UUID.fromString(currentUserId);

            boolean isOwner = cardRepository.existsByIdAndOwnerId(cardId, userId);

            log.debug("isCardOwner. Card ownership check - cardId: {}, userId: {}, isOwner: {}",
                cardId, userId, isOwner);

            return isOwner;

        } catch (Exception e) {
            log.error("isCardOwner. Error checking card ownership for cardId: {}", cardId, e);
            return false;
        }
    }

}
