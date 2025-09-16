package com.example.bankcards.utility.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessagesConstant {

    public static final String CARD_NOT_FOUND_BY_ID_MESSAGE = "Card not found by id: %s";
    public static final String CARD_EXPIRED = "Card has expired. Card id: %s";
    public static final String CARD_BLOCKED = "Card is blocked. Card id: %s";
    public static final String INSUFFICIENT_BALANCE = "Insufficient balance";
    public static final String TRANSFER_SAME_CARD = "Cannot transfer to the same card";
    public static final String CARD_DELETE_HAS_POSITIVE_BALANCE =
        "Cannot delete card with positive balance. Card id: %s";
    public static final String CARD_ALREADY_EXISTS_BY_CARD_NUMBER = "Card already exists";

    public static final String USER_NOT_FOUND_BY_ID_MESSAGE = "User not found by id: %s";
    public static final String USER_ALREADY_EXISTS_BY_EMAIL_MESSAGE = "User already exists by email: %s";
    public static final String CANNOT_UPDATE_DEACTIVATED_USER = "User can't update deactivated user";
    public static final String USER_ALREADY_DEACTIVATED = "User already deactivated";
    public static final String CANNOT_DEACTIVATED_ADMIN = "Can't deactivate administrator";
    public static final String USER_ALREADY_ACTIVE = "User already active";
    public static final String CANNOT_DELETE_USER_WITH_CARDS = "Cannot delete user with card";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String USER_NOT_FOUND_BY_EMAIL = "User not found by email: %s";

}
