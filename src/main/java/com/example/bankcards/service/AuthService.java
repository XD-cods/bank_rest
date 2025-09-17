package com.example.bankcards.service;

import com.example.bankcards.dto.request.AuthRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.AuthResponse;

public interface AuthService {

    void registerNewUser(UserRequest userRequest);

    AuthResponse loginUser(AuthRequest authRequest);
}
