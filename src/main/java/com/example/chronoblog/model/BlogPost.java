package com.example.chronoblog.model;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Represents the 'blogPosts' collection in MongoDB.
 * Each document stores a single blog post, including its time capsule information.
 */
@Document(collection = "blogPosts")
@Data
public class BlogPost {

    @Id
    private String id;

    private String title;

    private String content; // Will store the HTML content from the rich text editor.

    private String authorId; // A reference to the User's _id who wrote this post.

    private String author; // The author's username for display purposes

    private PostStatus status; // The current state of the post (e.g., DRAFT, PUBLISHED, SCHEDULED).

    private Instant publishAt; // The timestamp for when the post should be or was published.

    @JsonProperty("isPrivate")
    private boolean isPrivate; // Whether the post is private (only visible to author) or public

    // private boolean isTimeCapsule; // Add this line

    private int likesCount; // Number of likes on the post

    private int commentsCount; // Number of comments on the post

    private Set<String> likedBy; // Set of user IDs who liked this post
    
    // private String fileUrl; // To store the URL of the uploaded file

    @CreatedDate // Automatically populated by Spring Data MongoDB when the document is first saved.
    private Instant createdAt;

    @LastModifiedDate // Automatically populated by Spring Data MongoDB when the document is updated.
    private Instant updatedAt;
    
}