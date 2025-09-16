package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.error.UserNotFoundByEmail;
import com.example.bankcards.exception.error.UserNotFoundById;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.utility.constant.ErrorMessagesConstant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        log.debug("loadUserByUsername. Loading user by username/email: {}", username);

        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> {
                log.error("loadUserByUsername. User not found by email: {}", username);
                return new UserNotFoundByEmail(ErrorMessagesConstant.USER_NOT_FOUND_BY_EMAIL.formatted(username));
            });

        log.info("loadUserByUsername. User loaded successfully: {}", username);
        return CustomUserDetails.create(user);
    }

    @Transactional
    public UserDetails loadUserById(UUID userId) {
        log.debug("loadUserById. Loading user by ID: {}", userId);
        User user = getUserById(userId);

        log.debug("loadUserById. User loaded successfully by ID: {}", userId);
        return CustomUserDetails.create(user);
    }

    private User getUserById(UUID userId) {
        log.debug("getUserById. Fetching user from repository by ID: {}", userId);

        return userRepository.findById(userId)
            .orElseThrow(
                () -> {
                    log.error("getUserById. User not found by id: {}", userId);
                    return new UserNotFoundById(ErrorMessagesConstant.USER_NOT_FOUND_BY_ID_MESSAGE.formatted(userId));
                });
    }

}
