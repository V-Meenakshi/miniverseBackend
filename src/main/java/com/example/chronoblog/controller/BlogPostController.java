package com.example.chronoblog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.chronoblog.dto.CommentRequest;
import com.example.chronoblog.dto.CommentResponse;
import com.example.chronoblog.dto.PostRequest;
import com.example.chronoblog.model.BlogPost;
import com.example.chronoblog.model.User;
import com.example.chronoblog.repository.UserRepository;
import com.example.chronoblog.service.BlogPostService;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import com.example.chronoblog.exception.ResourceNotFoundException;
import com.example.chronoblog.exception.UnauthorizedException;
import com.example.chronoblog.scheduler.PublishingScheduler;

@RestController

@RequestMapping("/api/posts")
// The @CrossOrigin annotation has been removed from here.
public class BlogPostController {

    @Autowired
    private BlogPostService blogPostService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PublishingScheduler publishingScheduler;

    @GetMapping("/public")
    public Page<BlogPost> getAllPublicPosts(@PageableDefault(sort = "publishAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return blogPostService.getAllPublicPosts(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getPostById(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        BlogPost post = blogPostService.getPostById(id, userDetails);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    
    public ResponseEntity<BlogPost> createPost(@Valid @RequestBody PostRequest postRequest, @AuthenticationPrincipal UserDetails userDetails) {
        BlogPost createdPost = blogPostService.createPost(postRequest, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
    
    @GetMapping("/me")
    
    public Page<BlogPost> getCurrentUserPosts(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return blogPostService.getPostsByAuthor(user.getId(), pageable);
    }

    @PutMapping("/{id}")
    
    public ResponseEntity<BlogPost> updatePost(@PathVariable String id, @Valid @RequestBody PostRequest postRequest, @AuthenticationPrincipal UserDetails userDetails) {
        BlogPost updatedPost = blogPostService.updatePost(id, postRequest, userDetails);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            blogPostService.deletePost(id, userDetails);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Manual trigger for publishing scheduled posts (for testing purposes)
     */
    @PostMapping("/trigger-publish")
    public ResponseEntity<?> triggerPublishScheduledPosts() {
        try {
            // This will manually trigger the publishing of scheduled posts
            publishingScheduler.publishScheduledPosts();
            return ResponseEntity.ok().body("Scheduled posts publishing triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error triggering scheduled posts publishing: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/like")
    
    public ResponseEntity<BlogPost> toggleLike(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        BlogPost updatedPost = blogPostService.toggleLike(id, userDetails);
        return ResponseEntity.ok(updatedPost);
    }

    @PostMapping("/{id}/comments")
    
    public ResponseEntity<CommentResponse> addComment(@PathVariable String id, @Valid @RequestBody CommentRequest commentRequest, @AuthenticationPrincipal UserDetails userDetails) {
        CommentResponse comment = blogPostService.addComment(id, commentRequest, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable String id) {
        List<CommentResponse> comments = blogPostService.getComments(id);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/comments/{commentId}")
    
    public ResponseEntity<?> deleteComment(@PathVariable String commentId, @AuthenticationPrincipal UserDetails userDetails) {
        blogPostService.deleteComment(commentId, userDetails);
        return ResponseEntity.noContent().build();
    }
    // Add this new method to your BlogPostController

    @GetMapping("/time-capsules")
    @PreAuthorize("hasAuthority('ROLE_BLOGGER')")
    public Page<BlogPost> getCurrentUserTimeCapsules(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return blogPostService.getTimeCapsulesByAuthor(user.getId(), pageable);
    }
    // Add these two new methods to your BlogPostController

    @GetMapping("/me/public")
    @PreAuthorize("hasAuthority('ROLE_BLOGGER')")
    public Page<BlogPost> getCurrentUserPublicPosts(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return blogPostService.getPublicPostsByAuthor(user.getId(), pageable);
    }

    @GetMapping("/me/private")
    @PreAuthorize("hasAuthority('ROLE_BLOGGER')")
    public Page<BlogPost> getCurrentUserPrivatePosts(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return blogPostService.getPrivatePostsByAuthor(user.getId(), pageable);
    }
}
