package com.example.bankcards.controller;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.PageResponse;
import com.example.bankcards.dto.response.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.security.Principal;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserController {

    UserResponse getUserDetails(@PathVariable UUID userId);

    PageResponse<UserResponse> getAllUsers(
        @RequestParam(required = false, defaultValue = "0") @Min(0) Integer currentPage,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer limit,
        @RequestParam(required = false) String search
    );

    UserResponse createUser(@Valid @RequestBody UserRequest userRequest);

    UserResponse updateUserDetails(
        @PathVariable UUID userId,
        @Valid @RequestBody UserRequest updateUserRequest
    );

    void deactivateUser(@PathVariable UUID userId);

    void activateUser(@PathVariable UUID userId);

    void deleteUser(@PathVariable UUID userId);

    UserResponse getCurrentUserDetails(Principal principal);

}
