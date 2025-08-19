package com.example.chronoblog.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class CommentResponse {
    private String id;
    private String postId;
    private String authorId;
    private String authorUsername;
    private String content;
    private Instant createdAt;
}
