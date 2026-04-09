package com.diamondanalytics.backend.service;

import com.diamondanalytics.backend.dto.AuthResponse;
import com.diamondanalytics.backend.dto.LoginRequest;
import com.diamondanalytics.backend.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}