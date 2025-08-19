package com.example.chronoblog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for handling user login requests.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}