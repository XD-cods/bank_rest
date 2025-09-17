package com.example.bankcards.service.impl;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardHashService {

    private final PasswordEncoder passwordEncoder;
    private final CardRepository cardRepository;
    private MessageDigest sha256Digest;

    {
        try {
            sha256Digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String hashCardNumber(String cardNumber) {
        return passwordEncoder.encode(cardNumber);
    }

    public String getQuickHash(String cardNumber) {
        byte[] hash = sha256Digest.digest(cardNumber.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    public boolean isCardNumberUnique(String cardNumber) {
        String quickHash = getQuickHash(cardNumber);
        List<Card> potentialMatches = cardRepository.findByQuickHash(quickHash);

        return potentialMatches.stream()
            .noneMatch(card -> passwordEncoder.matches(cardNumber, card.getCardNumberHash()));
    }

}
