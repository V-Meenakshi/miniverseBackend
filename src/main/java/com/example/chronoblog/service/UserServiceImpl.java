package com.example.chronoblog.service;

import com.example.chronoblog.dto.UpdateProfileRequest;
import com.example.chronoblog.dto.UpdatePasswordRequest;
import com.example.chronoblog.dto.UserProfileDto;
import com.example.chronoblog.exception.ResourceNotFoundException;
import com.example.chronoblog.exception.UnauthorizedException;
import com.example.chronoblog.model.PostStatus;
import com.example.chronoblog.model.User;
import com.example.chronoblog.repository.BlogPostRepository;
import com.example.chronoblog.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserProfileDto getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return convertToDto(user);
    }

    @Override
    public UserProfileDto getCurrentUserProfile(UserDetails currentUser) {
        User user = getUserByEmail(currentUser.getUsername());
        return convertToDto(user);
    }

    @Override
    public UserProfileDto updateCurrentUserProfile(UserDetails currentUser, UpdateProfileRequest updateRequest) {
        User user = getUserByEmail(currentUser.getUsername());

        user.setFullName(updateRequest.getFullName());
        user.setBio(updateRequest.getBio());
        user.setProfileImageUrl(updateRequest.getProfileImageUrl());

        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    public void updatePassword(UserDetails currentUser, UpdatePasswordRequest updateRequest) {
        User user = getUserByEmail(currentUser.getUsername());
        
        // Verify current password
        if (!passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }
        
        // Update to new password
        user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteAccount(UserDetails currentUser) {
        User user = getUserByEmail(currentUser.getUsername());
        
        // Delete all user's blog posts first
        blogPostRepository.deleteByAuthorId(user.getId());
        
        // Delete the user account
        userRepository.delete(user);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    // Updated to populate all new fields
    private UserProfileDto convertToDto(User user) {
        UserProfileDto userProfileDto = new UserProfileDto();
        BeanUtils.copyProperties(user, userProfileDto);

        // Set the new fields
        userProfileDto.setJoinedDate(user.getCreatedAt());
        userProfileDto.setEmail(user.getEmail());

        // Calculate post counts
        long publishedCount = blogPostRepository.countByAuthorIdAndStatus(user.getId(), PostStatus.PUBLISHED);
        long scheduledCount = blogPostRepository.countByAuthorIdAndStatus(user.getId(), PostStatus.SCHEDULED);

        userProfileDto.setPublishedPostsCount(publishedCount);
        userProfileDto.setTimeCapsulesCount(scheduledCount);

        return userProfileDto;
    }
}