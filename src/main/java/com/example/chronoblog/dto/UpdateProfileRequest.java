package com.example.chronoblog.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(max = 50)
    private String fullName;

    @Size(max = 500)
    private String bio;

    private String profileImageUrl;
}