package com.example.bankcards.exception.error;

public class UserAlreadyExistsByEmailException extends RuntimeException {
    public UserAlreadyExistsByEmailException(String message) {
        super(message);
    }
}
