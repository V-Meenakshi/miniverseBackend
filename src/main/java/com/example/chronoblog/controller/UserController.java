package com.example.chronoblog.controller;

import com.example.chronoblog.dto.UpdateProfileRequest;
import com.example.chronoblog.dto.UpdatePasswordRequest;
import com.example.chronoblog.dto.UserProfileDto;
import com.example.chronoblog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Public endpoint to view any user's profile
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String username) {
        UserProfileDto userProfile = userService.getUserProfileByUsername(username);
        return ResponseEntity.ok(userProfile);
    }

    // Protected endpoint for the logged-in user to get their own profile
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_BLOGGER')")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile(@AuthenticationPrincipal UserDetails currentUser) {
        UserProfileDto userProfile = userService.getCurrentUserProfile(currentUser);
        return ResponseEntity.ok(userProfile);
    }

    // Protected endpoint for the logged-in user to update their own profile
    @PutMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_BLOGGER')")
    public ResponseEntity<UserProfileDto> updateCurrentUserProfile(@AuthenticationPrincipal UserDetails currentUser, @Valid @RequestBody UpdateProfileRequest updateRequest) {
        UserProfileDto updatedProfile = userService.updateCurrentUserProfile(currentUser, updateRequest);
        return ResponseEntity.ok(updatedProfile);
    }

    // Protected endpoint for the logged-in user to update their password
    @PutMapping("/me/password")
    @PreAuthorize("hasAuthority('ROLE_BLOGGER')")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal UserDetails currentUser, @Valid @RequestBody UpdatePasswordRequest updateRequest) {
        userService.updatePassword(currentUser, updateRequest);
        return ResponseEntity.ok().body("Password updated successfully");
    }

    // Protected endpoint for the logged-in user to delete their account
    @DeleteMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_BLOGGER')")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserDetails currentUser) {
        userService.deleteAccount(currentUser);
        return ResponseEntity.ok().body("Account deleted successfully");
    }
}
