package com.example.bankcards.service;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.PageResponse;
import com.example.bankcards.dto.response.UserResponse;
import java.util.UUID;

public interface UserService {

    UserResponse getUserDetails(UUID userId);

    PageResponse<UserResponse> getAllUsers(Integer currentPage, Integer limit, String search);

    UserResponse createUser(UserRequest userRequest);

    UserResponse updateUserDetails(UUID userId, UserRequest updateUserRequest);

    void deactivateUser(UUID userId);

    void activateUser(UUID userId);

    void deleteUser(UUID userId);

}
