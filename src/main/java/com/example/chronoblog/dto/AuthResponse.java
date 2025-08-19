package com.example.chronoblog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for sending a successful authentication response back to the client.
 * It contains the JWT and some basic user information.
 */
@Data
@AllArgsConstructor // Lombok annotation for a constructor with all fields.
public class AuthResponse {
    private String token;
    private String username;
}