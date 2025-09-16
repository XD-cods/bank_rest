package com.example.bankcards.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardHashService {
    private final PasswordEncoder passwordEncoder;

    public String hashCardNumber(String cardNumber) {
        return passwordEncoder.encode(cardNumber);
    }

    public boolean matches(String cardNumber, String hash) {
        return passwordEncoder.matches(cardNumber, hash);
    }

}
