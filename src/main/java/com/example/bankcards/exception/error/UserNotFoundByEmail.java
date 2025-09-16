package com.example.bankcards.exception.error;

public class UserNotFoundByEmail extends RuntimeException {
    public UserNotFoundByEmail(String message) {
        super(message);
    }
}
