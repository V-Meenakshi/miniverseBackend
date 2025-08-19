package com.example.chronoblog.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Represents comments on blog posts.
 */
@Document(collection = "comments")
@Data
public class Comment {

    @Id
    private String id;

    private String postId; // Reference to the blog post

    private String authorId; // Reference to the user who wrote the comment

    private String authorUsername; // Username of the comment author

    private String content; // The comment text

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
