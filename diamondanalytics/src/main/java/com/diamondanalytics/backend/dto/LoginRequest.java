package com.diamondanalytics.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username;  // o email

    @NotBlank
    private String password;
}