package com.example.bankcards.service.impl;


import com.example.bankcards.dto.request.AuthRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.error.BadRequestException;
import com.example.bankcards.exception.error.UserAlreadyExistsByEmailException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.utility.constant.ErrorMessagesConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public void registerNewUser(UserRequest userRequest) {
        log.debug("registerNewUser. Attempting to register new user with email: {}", userRequest.email());

        if (userRepository.existsByEmail(userRequest.email())) {
            log.error("registerNewUser. User already exists with email: {}", userRequest.email());
            throw new UserAlreadyExistsByEmailException(
                ErrorMessagesConstant.USER_ALREADY_EXISTS_BY_EMAIL_MESSAGE.formatted(userRequest.email())
            );
        }

        String encryptedPassword = passwordEncoder.encode(userRequest.password());

        User user = User.builder()
            .firstName(userRequest.firstName())
            .lastName(userRequest.lastName())
            .email(userRequest.email())
            .encryptedPassword(encryptedPassword)
            .role(Role.ROLE_USER)
            .isActive(true)
            .build();

        User savedUser = userRepository.save(user);

        log.info("registerNewUser. User registered successfully. User ID: {}", savedUser.getId());
    }

    public AuthResponse loginUser(AuthRequest authRequest) {
        log.debug("loginUser. Attempting to login user with email: {}", authRequest.email());

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.email(),
                    authRequest.password()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtTokenProvider.generateToken(authentication);

            log.info("loginUser. User logged is successfully. Email: {}", authRequest.email());
            return new AuthResponse(jwt);

        } catch (Exception e) {
            log.error("loginUser. Authentication failed for email: {}", authRequest.email(), e);
            throw new BadRequestException(ErrorMessagesConstant.INVALID_CREDENTIALS);
        }
    }

}
