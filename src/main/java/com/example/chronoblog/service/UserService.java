package com.example.chronoblog.service;

import com.example.chronoblog.dto.UpdateProfileRequest;
import com.example.chronoblog.dto.UpdatePasswordRequest;
import com.example.chronoblog.dto.UserProfileDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserProfileDto getUserProfileByUsername(String username);
    UserProfileDto getCurrentUserProfile(UserDetails currentUser);
    UserProfileDto updateCurrentUserProfile(UserDetails currentUser, UpdateProfileRequest updateRequest);
    void updatePassword(UserDetails currentUser, UpdatePasswordRequest updateRequest);
    void deleteAccount(UserDetails currentUser);
}