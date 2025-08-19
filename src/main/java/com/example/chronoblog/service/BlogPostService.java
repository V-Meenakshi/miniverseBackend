package com.example.chronoblog.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.chronoblog.dto.CommentRequest;
import com.example.chronoblog.dto.CommentResponse;
import com.example.chronoblog.dto.PostRequest;
import com.example.chronoblog.model.BlogPost;

/**
 * Interface for the BlogPost service layer.
 * It defines the contract for all business logic related to blog posts.
 * This separation allows for different implementations and makes testing easier.
 */
public interface BlogPostService {

    /**
     * Retrieves a paginated list of all public posts with the status 'PUBLISHED'.
     *
     * @param pageable Pagination and sorting information.
     * @return A Page of published blog posts.
     */
    Page<BlogPost> getAllPublicPosts(Pageable pageable);

    /**
     * Retrieves a single blog post by its unique ID.
     *
     * @param id The ID of the post.
     * @return The found BlogPost.
     */
    BlogPost getPostById(String id);

    /**
     * Retrieves a single blog post by its unique ID with user context for private post access.
     *
     * @param id The ID of the post.
     * @param userDetails The details of the currently authenticated user.
     * @return The found BlogPost.
     */
    BlogPost getPostById(String id, UserDetails userDetails);

    /**
     * Retrieves a paginated list of all posts by a specific author.
     *
     * @param authorId The ID of the author.
     * @param pageable Pagination and sorting information.
     * @return A Page of the author's posts.
     */
    Page<BlogPost> getPostsByAuthor(String authorId, Pageable pageable);

    /**
     * Creates a new blog post. Sets status to SCHEDULED if publishAt is in the future.
     *
     * @param postRequest The DTO containing the new post's data.
     * @param userDetails The details of the currently authenticated user.
     * @return The newly created and saved BlogPost.
     */
    BlogPost createPost(PostRequest postRequest, UserDetails userDetails);

    /**
     * Updates an existing blog post. Verifies that the user is the author.
     *
     * @param id The ID of the post to update.
     * @param postRequest The DTO containing the updated data.
     * @param userDetails The details of the currently authenticated user.
     * @return The updated BlogPost.
     */
    BlogPost updatePost(String id, PostRequest postRequest, UserDetails userDetails);

    /**
     * Deletes a blog post. Verifies that the user is the author.
     *
     * @param id The ID of the post to delete.
     * @param userDetails The details of the currently authenticated user.
     */
    void deletePost(String id, UserDetails userDetails);

    /**
     * Likes or unlikes a blog post.
     *
     * @param postId The ID of the post to like/unlike.
     * @param userDetails The details of the currently authenticated user.
     * @return The updated BlogPost with like count.
     */
    BlogPost toggleLike(String postId, UserDetails userDetails);

    /**
     * Adds a comment to a blog post.
     *
     * @param postId The ID of the post to comment on.
     * @param commentRequest The comment data.
     * @param userDetails The details of the currently authenticated user.
     * @return The created comment.
     */
    CommentResponse addComment(String postId, CommentRequest commentRequest, UserDetails userDetails);

    /**
     * Retrieves all comments for a blog post.
     *
     * @param postId The ID of the post.
     * @return List of comments for the post.
     */
    List<CommentResponse> getComments(String postId);

    /**
     * Deletes a comment. Verifies that the user is the comment author.
     *
     * @param commentId The ID of the comment to delete.
     * @param userDetails The details of the currently authenticated user.
     */
    void deleteComment(String commentId, UserDetails userDetails);
}