package com.example.bankcards.exception.error;

public class CardNotFoundById extends RuntimeException {
    public CardNotFoundById(String message) {
        super(message);
    }
}
