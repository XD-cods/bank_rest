package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.UserController;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.PageResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public UserResponse getUserDetails(@PathVariable UUID userId) {
        return userService.getUserDetails(userId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> getAllUsers(
        @RequestParam(required = false, defaultValue = "0") @Min(0) Integer currentPage,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer limit,
        @RequestParam(required = false) String search
    ) {
        return userService.getAllUsers(currentPage, limit, search);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public UserResponse updateUserDetails(
        @PathVariable UUID userId,
        @Valid @RequestBody UserRequest updateUserRequest
    ) {
        return userService.updateUserDetails(userId, updateUserRequest);
    }

    @PatchMapping("/{userId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public void deactivateUser(@PathVariable UUID userId) {
        userService.deactivateUser(userId);
    }

    @PatchMapping("/{userId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void activateUser(@PathVariable UUID userId) {
        userService.activateUser(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }

}
