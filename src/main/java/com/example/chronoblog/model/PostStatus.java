package com.example.chronoblog.model;

/**
 * An enumeration to represent the possible states of a blog post.
 * Using an enum provides type safety and makes the code more readable and robust
 * compared to using simple strings.
 */
public enum PostStatus {
    /**
     * The post is saved but not visible to the public.
     * (Future feature - not implemented in the current controller)
     */
    DRAFT,

    /**
     * The post is live and visible to everyone.
     */
    PUBLISHED,

    /**
     * The post is a time capsule, waiting for its publishAt date to arrive.
     * The scheduler will pick up posts with this status.
     */
    SCHEDULED
}
