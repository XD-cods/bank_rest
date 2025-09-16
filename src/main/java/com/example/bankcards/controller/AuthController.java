package com.example.bankcards.controller;

import com.example.bankcards.dto.request.AuthRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthController {

    void register(@Valid @RequestBody UserRequest authRequest);

    AuthResponse login(@Valid @RequestBody AuthRequest authRequest);

}
