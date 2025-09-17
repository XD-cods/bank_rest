package com.example.bankcards.unit.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.bankcards.constant.UnitTestDataProvider;
import com.example.bankcards.dto.request.AuthRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.error.BadRequestException;
import com.example.bankcards.exception.error.UserAlreadyExistsByEmailException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.service.impl.AuthServiceImpl;
import com.example.bankcards.utility.constant.ErrorMessagesConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserRequest userRequest;
    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        userRequest = UnitTestDataProvider.userRequest();
        authRequest = UnitTestDataProvider.authRequest();
        user = UnitTestDataProvider.user();
    }

    @Test
    void registerNewUser_shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(userRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.password())).thenReturn(UnitTestDataProvider.TEST_HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.registerNewUser(userRequest);

        verify(userRepository).existsByEmail(userRequest.email());
        verify(passwordEncoder).encode(userRequest.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerNewUser_shouldThrowUserAlreadyExistsException() {
        when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);

        UserAlreadyExistsByEmailException exception = assertThrows(
            UserAlreadyExistsByEmailException.class,
            () -> authService.registerNewUser(userRequest)
        );

        assertEquals(
            ErrorMessagesConstant.USER_ALREADY_EXISTS_BY_EMAIL_MESSAGE.formatted(userRequest.email()),
            exception.getMessage()
        );

        verify(userRepository).existsByEmail(userRequest.email());
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_shouldLoginSuccessfully() {
        UsernamePasswordAuthenticationToken authentication = UnitTestDataProvider.authenticationForLogin();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(
            authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(UnitTestDataProvider.TEST_JWT_TOKEN);

        AuthResponse result = authService.loginUser(authRequest);

        assertNotNull(result);
        assertEquals(UnitTestDataProvider.TEST_JWT_TOKEN, result.token());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(authentication);
    }

    @Test
    void loginUser_shouldThrowBadRequestExceptionForInvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadRequestException(ErrorMessagesConstant.INVALID_CREDENTIALS));

        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> authService.loginUser(authRequest)
        );

        assertEquals(ErrorMessagesConstant.INVALID_CREDENTIALS, exception.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtTokenProvider);
    }
}