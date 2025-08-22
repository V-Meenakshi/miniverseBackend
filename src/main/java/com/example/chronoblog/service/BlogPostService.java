package com.example.chronoblog.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.chronoblog.dto.CommentRequest;
import com.example.chronoblog.dto.CommentResponse;
import com.example.chronoblog.dto.PostRequest;
import com.example.chronoblog.model.BlogPost;

public interface BlogPostService {

    Page<BlogPost> getAllPublicPosts(Pageable pageable);

    BlogPost getPostById(String id);

    BlogPost getPostById(String id, UserDetails userDetails);

    Page<BlogPost> getPostsByAuthor(String authorId, Pageable pageable);

    // Methods for fetching public, private, and time capsule posts
    Page<BlogPost> getPublicPostsByAuthor(String authorId, Pageable pageable);
    Page<BlogPost> getPrivatePostsByAuthor(String authorId, Pageable pageable);
    Page<BlogPost> getTimeCapsulesByAuthor(String authorId, Pageable pageable);

    BlogPost createPost(PostRequest postRequest, UserDetails userDetails);

    BlogPost updatePost(String id, PostRequest postRequest, UserDetails userDetails);

    void deletePost(String id, UserDetails userDetails);

    BlogPost toggleLike(String postId, UserDetails userDetails);

    CommentResponse addComment(String postId, CommentRequest commentRequest, UserDetails userDetails);

    List<CommentResponse> getComments(String postId);

    void deleteComment(String commentId, UserDetails userDetails);
}