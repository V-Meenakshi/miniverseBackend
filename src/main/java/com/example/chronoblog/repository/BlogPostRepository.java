package com.example.chronoblog.repository;

import com.example.chronoblog.model.BlogPost;
import com.example.chronoblog.model.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.Instant;
import java.util.List;

/**
 * Repository interface for the BlogPost model.
 * Provides CRUD operations and custom queries for blog posts.
 */
public interface BlogPostRepository extends MongoRepository<BlogPost, String> {

    /**
     * Finds a paginated list of posts with a specific status.
     * Used for fetching the public feed of all PUBLISHED posts.
     *
     * @param status The status of the posts to find (e.g., PUBLISHED).
     * @param pageable An object containing pagination and sorting information.
     * @return A Page object containing the list of posts for the current page.
     */
    Page<BlogPost> findByStatus(PostStatus status, Pageable pageable);

    /**
     * Finds a paginated list of public posts with a specific status.
     * Used for fetching only public published posts.
     *
     * @param status The status of the posts to find (e.g., PUBLISHED).
     * @param isPrivate Whether the posts are private (false for public posts).
     * @param pageable An object containing pagination and sorting information.
     * @return A Page object containing the list of public posts for the current page.
     */
    Page<BlogPost> findByStatusAndIsPrivateFalse(PostStatus status, Pageable pageable);

    /**
     * Finds a paginated list of public posts with multiple statuses.
     * Used for fetching public posts that are either published or scheduled.
     *
     * @param statuses List of statuses to include (e.g., PUBLISHED, SCHEDULED).
     * @param pageable An object containing pagination and sorting information.
     * @return A Page object containing the list of public posts for the current page.
     */
    Page<BlogPost> findByIsPrivateFalseAndStatusIn(List<PostStatus> statuses, Pageable pageable);

    /**
     * Finds all posts by a specific author, with pagination.
     * Used for the user's dashboard ("My Posts").
     *
     * @param authorId The ID of the author.
     * @param pageable Pagination and sorting information.
     * @return A Page of the author's posts.
     */
    Page<BlogPost> findByAuthorId(String authorId, Pageable pageable);

    /**
     * This is the core query for the time capsule feature.
     * It finds all posts that are SCHEDULED and whose publication time has passed.
     *
     * @param status The status to search for (will always be SCHEDULED).
     * @param now The current time. The query will find posts where publishAt is before this time.
     * @return A List of BlogPost objects that are ready to be published.
     */
    List<BlogPost> findByStatusAndPublishAtBefore(PostStatus status, Instant now);
    long countByAuthorIdAndStatus(String authorId, PostStatus status);
    
    /**
     * Deletes all posts by a specific author.
     * Used when deleting a user account to clean up their posts.
     *
     * @param authorId The ID of the author whose posts should be deleted.
     */
    void deleteByAuthorId(String authorId);
    // Add this new method to your BlogPostRepository
    Page<BlogPost> findByAuthorIdAndStatus(String authorId, PostStatus status, Pageable pageable);
    // Add this new method to your BlogPostRepository
    Page<BlogPost> findByAuthorIdAndIsPrivate(String authorId, boolean isPrivate, Pageable pageable);   
    /**
     * Finds a paginated list of posts by a specific author and a list of statuses.
     * This is the key to excluding time capsules from the "My Blogs" page.
     *
     * @param authorId The ID of the author.
     * @param statuses A list of statuses to include (e.g., PUBLISHED, DRAFT).
     * @param pageable Pagination and sorting information.
     * @return A Page of the author's posts with the specified statuses.
     */
    Page<BlogPost> findByAuthorIdAndStatusIn(String authorId, List<PostStatus> statuses, Pageable pageable);

}
