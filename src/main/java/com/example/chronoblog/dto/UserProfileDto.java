package com.example.chronoblog.dto;

import com.example.chronoblog.model.AccountStatus;
import lombok.Data;
import java.time.Instant;

@Data
public class UserProfileDto {
    private String id;
    private String username;
    private String email; // Added email
    private String fullName;
    private String bio;
    private String profileImageUrl;
    private long publishedPostsCount; // Added published post count
    private long timeCapsulesCount;   // Added time capsule count
    private AccountStatus accountStatus; // Added account status
    private Instant joinedDate;       // Added joined date
}