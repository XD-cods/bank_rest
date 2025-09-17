package com.example.bankcards.exception.error;

public class UserNotFoundById extends RuntimeException {
    public UserNotFoundById(String message) {
        super(message);
    }
}
