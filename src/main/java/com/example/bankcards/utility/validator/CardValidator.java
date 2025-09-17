package com.example.bankcards.utility.validator;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.error.BadRequestException;
import com.example.bankcards.utility.constant.ErrorMessagesConstant;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CardValidator {

    public void validateCardsForTransfer(Card sourceCard, Card targetCard) {
        if (sourceCard.isExpired()) {
            log.error("validateCardsForTransfer. Card expired. Card ID: {}", sourceCard.getId());
            throw new BadRequestException(ErrorMessagesConstant.CARD_EXPIRED.formatted(sourceCard.getId()));
        }

        if (targetCard.isExpired()) {
            log.error("validateCardsForTransfer. Card expired. Card ID: {}", targetCard.getId());
            throw new BadRequestException(ErrorMessagesConstant.CARD_EXPIRED.formatted(targetCard.getId()));
        }

        if (sourceCard.getCardStatus() == CardStatus.BLOCKED) {
            log.error("validateCardsForTransfer. Card status is BLOCKED. Card ID: {}", sourceCard.getId());
            throw new BadRequestException(ErrorMessagesConstant.CARD_BLOCKED.formatted(sourceCard.getId()));
        }

        if (targetCard.getCardStatus() == CardStatus.BLOCKED) {
            log.error("validateCardsForTransfer. Card status is BLOCKED. Card ID: {}", targetCard.getId());
            throw new BadRequestException(ErrorMessagesConstant.CARD_BLOCKED.formatted(targetCard.getId()));
        }
    }

    public void validateCardForBlock(Card blockableCard) {
        if (blockableCard.isExpired()) {
            log.error("validateCardForBlock. Cannot block expired card: {}", blockableCard);
            throw new BadRequestException(ErrorMessagesConstant.CARD_EXPIRED);
        }
    }

    public void validateCardForUnlock(Card unlockedCard) {
        if (unlockedCard.isExpired()) {
            log.error("validateCardForUnlock. Cannot unlock expired card: {}", unlockedCard);
            throw new BadRequestException(ErrorMessagesConstant.CARD_EXPIRED);
        }
    }

    public void validateCardForDelete(Card card) {
        UUID cardId = card.getId();

        if (card.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            log.error("validateCardForDelete. Cannot delete card with positive balance. Card id: {}, balance: {}",
                cardId, card.getBalance());
            throw new BadRequestException(ErrorMessagesConstant.CARD_DELETE_HAS_POSITIVE_BALANCE.formatted(cardId));
        }
    }

    public void validateCardForUpade(Card card) {

        UUID cardId = card.getId();

        if (card.isExpired()) {
            log.warn("updateCardDetails. Cannot update expired card: {}", cardId);
            throw new BadRequestException(ErrorMessagesConstant.CARD_EXPIRED);
        }

        if (card.getCardStatus() == CardStatus.BLOCKED) {
            log.warn("updateCardDetails. Cannot update blocked card: {}", cardId);
            throw new BadRequestException(ErrorMessagesConstant.CARD_BLOCKED);
        }
    }

}
