package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.repository.CardRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferSecurityService {

    private final CardRepository cardRepository;

    public boolean canTransfer(TransferRequest transferRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("canTransfer. Unauthenticated transfer attempt");
                return false;
            }

            String currentUserId = authentication.getName();
            UUID userId = UUID.fromString(currentUserId);

            UUID sourceCardId = UUID.fromString(transferRequest.sourceCardId());
            UUID targetCardId = UUID.fromString(transferRequest.targetCardId());

            boolean canTransferSource = cardRepository.existsByIdAndOwnerId(sourceCardId, userId);
            boolean canTransferTarget = cardRepository.existsByIdAndOwnerId(targetCardId, userId);

            boolean canTransfer = canTransferSource && canTransferTarget;

            log.debug("canTransfer. Transfer security check - source: {}, target: {}, user: {}, allowed: {}",
                sourceCardId, targetCardId, userId, canTransfer);

            return canTransfer;

        } catch (Exception e) {
            log.error("canTransfer. Error in transfer security check", e);
            return false;
        }
    }
}
