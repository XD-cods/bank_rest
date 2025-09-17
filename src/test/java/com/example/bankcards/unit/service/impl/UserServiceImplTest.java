package com.example.bankcards.unit.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.bankcards.constant.UnitTestDataProvider;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.PageResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.error.UserAlreadyExistsByEmailException;
import com.example.bankcards.exception.error.UserNotFoundById;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.UserServiceImpl;
import com.example.bankcards.utility.constant.ErrorMessagesConstant;
import com.example.bankcards.utility.mapper.PageResponseMapper;
import com.example.bankcards.utility.mapper.UserMapper;
import com.example.bankcards.utility.validator.UserValidator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;
    private UserRequest userRequest;
    private UUID userId;

    @BeforeEach
    void setUp() {
        user = UnitTestDataProvider.user();
        userResponse = UnitTestDataProvider.userResponse(user);
        userRequest = UnitTestDataProvider.userRequest();
        userId = user.getId();
    }

    @Test
    void getUserDetails_shouldReturnUserResponse() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserDetails(userId);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository).findById(userId);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getUserDetails_shouldThrowUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundById exception = assertThrows(
            UserNotFoundById.class,
            () -> userService.getUserDetails(userId)
        );

        assertEquals(
            ErrorMessagesConstant.USER_NOT_FOUND_BY_ID_MESSAGE.formatted(userId),
            exception.getMessage()
        );

        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getAllUsers_shouldReturnPageResponse() {
        int currentPage = 0;
        int limit = 10;
        PageRequest pageRequest = PageRequest.of(currentPage, limit);

        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users, pageRequest, users.size());

        when(userRepository.findAll(pageRequest)).thenReturn(userPage);
        when(pageResponseMapper.toPageResponse(eq(userPage), eq(currentPage), any()))
            .thenReturn(new PageResponse<>(List.of(userResponse), currentPage, userPage.getTotalElements(),
                userPage.getTotalPages()));

        PageResponse<UserResponse> result = userService.getAllUsers(currentPage, limit, "");

        assertNotNull(result);
        assertEquals(users.size(), result.content().size());
        assertEquals(currentPage, result.currentPage());

        verify(userRepository).findAll(pageRequest);
        verify(pageResponseMapper).toPageResponse(eq(userPage), eq(currentPage), any());
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        when(userRepository.existsByEmail(userRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.password())).thenReturn(UnitTestDataProvider.TEST_HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(userRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository).existsByEmail(userRequest.email());
        verify(passwordEncoder).encode(userRequest.password());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponse(user);
    }

    @Test
    void createUser_shouldThrowUserAlreadyExistsException() {
        when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);

        UserAlreadyExistsByEmailException exception = assertThrows(
            UserAlreadyExistsByEmailException.class,
            () -> userService.createUser(userRequest)
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
    void updateUserDetails_shouldUpdateUserSuccessfully() {
        UserRequest updateRequest = UnitTestDataProvider.updateUserRequest();
        UserResponse updatedResponse = UnitTestDataProvider.updatedUserResponse(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(updateRequest.password())).thenReturn(
            UnitTestDataProvider.TEST_NEW_HASHED_PASSWORD);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserDetails(userId, updateRequest);

        assertNotNull(result);
        assertEquals(updatedResponse, result);

        verify(userRepository).findById(userId);
        verify(userValidator).validateUserForUpdate(user);
        verify(userRepository).existsByEmail(updateRequest.email());
        verify(passwordEncoder).encode(updateRequest.password());
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void updateUserDetails_shouldThrowUserAlreadyExistsException() {
        UserRequest updateRequest = UnitTestDataProvider.updateUserRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateRequest.email())).thenReturn(true);

        UserAlreadyExistsByEmailException exception = assertThrows(
            UserAlreadyExistsByEmailException.class,
            () -> userService.updateUserDetails(userId, updateRequest)
        );

        assertEquals(
            ErrorMessagesConstant.USER_ALREADY_EXISTS_BY_EMAIL_MESSAGE.formatted(updateRequest.email()),
            exception.getMessage()
        );

        verify(userRepository).findById(userId);
        verify(userValidator).validateUserForUpdate(user);
        verify(userRepository).existsByEmail(updateRequest.email());
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUserDetails_shouldUpdateUserWithoutPasswordChange() {
        UserRequest updateRequest = UnitTestDataProvider.updateUserRequestWithoutPassword();
        UserResponse updatedResponse = UnitTestDataProvider.updatedUserResponse(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateRequest.email())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserDetails(userId, updateRequest);

        assertNotNull(result);
        assertEquals(updatedResponse, result);

        verify(userRepository).findById(userId);
        verify(userValidator).validateUserForUpdate(user);
        verify(userRepository).existsByEmail(updateRequest.email());
        verifyNoInteractions(passwordEncoder);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void updateUserDetails_shouldThrowUserNotFoundException() {
        UserRequest updateRequest = UnitTestDataProvider.updateUserRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundById exception = assertThrows(
            UserNotFoundById.class,
            () -> userService.updateUserDetails(userId, updateRequest)
        );

        assertEquals(
            ErrorMessagesConstant.USER_NOT_FOUND_BY_ID_MESSAGE.formatted(userId),
            exception.getMessage()
        );

        verify(userRepository).findById(userId);
        verifyNoInteractions(userValidator, passwordEncoder, userMapper);
    }

    @Test
    void activateUser_shouldActivateUserSuccessfully() {
        User inactiveUser = UnitTestDataProvider.inactiveUser();
        when(userRepository.findById(inactiveUser.getId())).thenReturn(Optional.of(inactiveUser));
        when(userRepository.save(inactiveUser)).thenReturn(inactiveUser);

        userService.activateUser(inactiveUser.getId());

        assertEquals(true, inactiveUser.isActive());

        verify(userRepository).findById(inactiveUser.getId());
        verify(userValidator).validateUserForActivation(inactiveUser);
        verify(userRepository).save(inactiveUser);
    }

    @Test
    void deactivateUser_shouldDeactivateUserSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deactivateUser(userId);

        assertEquals(false, user.isActive());

        verify(userRepository).findById(userId);
        verify(userValidator).validateUserForDeactivation(user);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_shouldDeleteUserSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userValidator).validateUserForDelete(user);
        verify(userRepository).delete(user);
    }

    @Test
    void activateUser_shouldThrowUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundById exception = assertThrows(
            UserNotFoundById.class,
            () -> userService.activateUser(userId)
        );

        assertEquals(
            ErrorMessagesConstant.USER_NOT_FOUND_BY_ID_MESSAGE.formatted(userId),
            exception.getMessage()
        );

        verify(userRepository).findById(userId);
        verifyNoInteractions(userValidator);
        verify(userRepository, never()).save(any(User.class));
    }
}