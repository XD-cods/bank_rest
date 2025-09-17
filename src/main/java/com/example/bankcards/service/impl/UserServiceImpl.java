package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.PageResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.error.UserAlreadyExistsByEmailException;
import com.example.bankcards.exception.error.UserNotFoundById;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import com.example.bankcards.utility.constant.ErrorMessagesConstant;
import com.example.bankcards.utility.mapper.PageResponseMapper;
import com.example.bankcards.utility.mapper.UserMapper;
import com.example.bankcards.utility.validator.UserValidator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final PageResponseMapper pageResponseMapper;
    private final UserValidator userValidator;


    public UserResponse getUserDetails(UUID userId) {
        log.debug("getUserDetails. Entering method. User id: {}", userId);
        User user = getUserByUserId(userId);

        log.info("getUserDetails. User details founded. User id: {}", userId);
        return userMapper.toResponse(user);
    }

    public PageResponse<UserResponse> getAllUsers(Integer currentPage, Integer limit, String search) {
        log.debug("getAllUsers. Entering method. Current page: {}, limit: {}, search: {}", currentPage, limit, search);

        Pageable pageable = PageRequest.of(currentPage, limit);
        Page<User> pageOfUser = userRepository.findAll(pageable);

        PageResponse<UserResponse> pageResponseOfUser = pageResponseMapper.toPageResponse(
            pageOfUser,
            currentPage,
            userMapper::toResponse
        );

        log.info("getAllUsers. All page of users retrieved.");
        return pageResponseOfUser;
    }

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        log.debug("createUser. Entering method. User request: {}", userRequest);

        if (userRepository.existsByEmail(userRequest.email())) {
            log.error("createUser. User already exists with email: {}", userRequest.email());
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
            .isActive(true)
            .build();

        User savedUser = userRepository.save(user);

        log.info("createUser. User created successfully. User id: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUserDetails(UUID userId, UserRequest updateUserRequest) {
        log.debug("updateUserDetails. Entering method. User id: {}, request: {}", userId, updateUserRequest);

        User user = getUserByUserId(userId);

        userValidator.validateUserForUpdate(user);

        if (!user.getEmail().equals(updateUserRequest.email())
            && userRepository.existsByEmail(updateUserRequest.email())) {
            log.error("updateUserDetails. Email already exists: {}", updateUserRequest.email());
            throw new UserAlreadyExistsByEmailException(
                ErrorMessagesConstant.USER_ALREADY_EXISTS_BY_EMAIL_MESSAGE.formatted(updateUserRequest.email()));
        }

        user.setFirstName(updateUserRequest.firstName());
        user.setLastName(updateUserRequest.lastName());
        user.setEmail(updateUserRequest.email());

        if (updateUserRequest.password() != null && !updateUserRequest.password().isBlank()) {
            String encryptedPassword = passwordEncoder.encode(updateUserRequest.password());
            user.setEncryptedPassword(encryptedPassword);
        }

        User updatedUser = userRepository.save(user);
        log.info("updateUserDetails. User updated successfully. User id: {}", userId);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deactivateUser(UUID userId) {
        log.debug("deactivateUser. Entering method. User id: {}", userId);

        User user = getUserByUserId(userId);
        userValidator.validateUserForDeactivation(user);

        user.setActive(false);
        userRepository.save(user);

        log.info("deactivateUser. User deactivated successfully. User id: {}", userId);
    }

    @Transactional
    public void activateUser(UUID userId) {
        log.debug("activateUser. Entering method. User id: {}", userId);

        User user = getUserByUserId(userId);
        userValidator.validateUserForActivation(user);

        user.setActive(true);
        userRepository.save(user);

        log.info("activateUser. User activated successfully. User id: {}", userId);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        log.debug("deleteUser. Entering method. User id: {}", userId);

        User user = getUserByUserId(userId);
        userValidator.validateUserForDelete(user);

        userRepository.delete(user);
        log.info("deleteUser. User deleted successfully. User id: {}", userId);
    }

    private User getUserByUserId(UUID userId) {
        log.debug("getUserByUserId. Search user by user Id: {}", userId);

        return userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("getUserByUserId. User not found with id {}", userId);
                return new UserNotFoundById(ErrorMessagesConstant.USER_NOT_FOUND_BY_ID_MESSAGE.formatted(userId));
            });
    }

}
