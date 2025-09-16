package com.example.bankcards.exception.error;

public class CardAlreadyExistsByCardNumberException extends RuntimeException {
    public CardAlreadyExistsByCardNumberException(String message) {
        super(message);
    }
}
