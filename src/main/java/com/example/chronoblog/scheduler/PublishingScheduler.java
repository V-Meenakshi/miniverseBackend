package com.example.chronoblog.scheduler;

import com.example.chronoblog.model.BlogPost;
import com.example.chronoblog.model.PostStatus;
import com.example.chronoblog.repository.BlogPostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * A scheduled task that runs periodically to handle automated processes.
 * This scheduler is responsible for publishing time-capsuled blog posts.
 */
@Component // Marks this class as a Spring component so it's managed by the Spring container.
public class PublishingScheduler {

    // It's a best practice to use a logger to see the output of scheduled tasks.
    private static final Logger log = LoggerFactory.getLogger(PublishingScheduler.class);

    @Autowired
    private BlogPostRepository blogPostRepository;

    /**
     * This method runs at a fixed interval to check for and publish scheduled posts.
     * The `fixedRate = 60000` means it will run every 60,000 milliseconds (every 1 minute).
     */
    @Scheduled(fixedRate = 60000)
    public void publishScheduledPosts() {
        log.info("Checking for scheduled posts to publish...");

        // Find all posts that are 'SCHEDULED' and whose publish time is in the past.
        List<BlogPost> postsToPublish = blogPostRepository.findByStatusAndPublishAtBefore(PostStatus.SCHEDULED, Instant.now());

        if (postsToPublish.isEmpty()) {
            log.info("No posts to publish at this time.");
            return;
        }

        log.info("Found {} post(s) to publish.", postsToPublish.size());

        // Loop through each post, update its status, and save it back to the database.
        for (BlogPost post : postsToPublish) {
            post.setStatus(PostStatus.PUBLISHED);
            blogPostRepository.save(post);
            log.info("Published post: '{}' with ID: {}", post.getTitle(), post.getId());
        }
    }
}
