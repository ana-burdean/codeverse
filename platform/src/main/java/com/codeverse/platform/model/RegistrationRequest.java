package com.codeverse.platform.model;

import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class RegistrationRequest {
    @NotBlank(message = "username is required")
    private String username;
    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;
}