package com.example.chronoblog.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.chronoblog.dto.CommentRequest;
import com.example.chronoblog.dto.CommentResponse;
import com.example.chronoblog.dto.PostRequest;
import com.example.chronoblog.exception.ResourceNotFoundException;
import com.example.chronoblog.exception.UnauthorizedException;
import com.example.chronoblog.model.BlogPost;
import com.example.chronoblog.model.Comment;
import com.example.chronoblog.model.PostStatus;
import com.example.chronoblog.model.User;
import com.example.chronoblog.repository.BlogPostRepository;
import com.example.chronoblog.repository.CommentRepository;
import com.example.chronoblog.repository.UserRepository;

@Service
public class BlogPostServiceImpl implements BlogPostService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Page<BlogPost> getAllPublicPosts(Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findByIsPrivateFalseAndStatusIn(
            Arrays.asList(PostStatus.PUBLISHED),
            pageable
        );
        
        posts.getContent().forEach(post -> {
            if (post.getAuthor() == null && post.getAuthorId() != null) {
                User author = userRepository.findById(post.getAuthorId()).orElse(null);
                if (author != null) {
                    post.setAuthor(author.getUsername());
                }
            }
        });
        
        return posts;
    }

    @Override
    public Page<BlogPost> getPublicPostsByAuthor(String authorId, Pageable pageable) {
        return blogPostRepository.findByAuthorIdAndIsPrivate(authorId, false, pageable);
    }

    @Override
    public Page<BlogPost> getPrivatePostsByAuthor(String authorId, Pageable pageable) {
        return blogPostRepository.findByAuthorIdAndIsPrivate(authorId, true, pageable);
    }

    @Override
    public BlogPost getPostById(String id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", id));
        
        if (post.isPrivate()) {
            throw new UnauthorizedException("This post is private and not accessible.");
        }
        
        return post;
    }

    @Override
    public BlogPost getPostById(String id, UserDetails userDetails) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", id));
        
        if (post.getAuthor() == null && post.getAuthorId() != null) {
            User author = userRepository.findById(post.getAuthorId()).orElse(null);
            if (author != null) {
                post.setAuthor(author.getUsername());
            }
        }
        
        if (post.isPrivate()) {
            if (userDetails == null) {
                throw new UnauthorizedException("This post is private and not accessible.");
            }
            User user = getUserByEmail(userDetails.getUsername());
            if (!post.getAuthorId().equals(user.getId())) {
                throw new UnauthorizedException("This post is private and not accessible.");
            }
        }
        
        return post;
    }

    @Override
    public Page<BlogPost> getPostsByAuthor(String authorId, Pageable pageable) {
        //
        // --- THIS IS THE KEY CHANGE ---
        // We now fetch only PUBLISHED and DRAFT posts, excluding SCHEDULED ones.
        //
        List<PostStatus> statuses = Arrays.asList(PostStatus.PUBLISHED, PostStatus.DRAFT);
        return blogPostRepository.findByAuthorIdAndStatusIn(authorId, statuses, pageable);
    }

    @Override
    public Page<BlogPost> getTimeCapsulesByAuthor(String authorId, Pageable pageable) {
        return blogPostRepository.findByAuthorIdAndStatus(authorId, PostStatus.SCHEDULED, pageable);
    }

    @Override
    public BlogPost createPost(PostRequest postRequest, UserDetails userDetails) {
        User user = getUserByEmail(userDetails.getUsername());

        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(postRequest.getTitle());
        blogPost.setContent(postRequest.getContent());
        blogPost.setAuthorId(user.getId());
        blogPost.setAuthor(user.getUsername());
        blogPost.setPrivate(postRequest.isPrivate());
        blogPost.setLikesCount(0);
        blogPost.setCommentsCount(0);
        blogPost.setLikedBy(new HashSet<>());
        // blogPost.setFileUrl(postRequest.getFileUrl()); // REMOVED

        // Handle status logic
        if (postRequest.getStatus() != null) {
            blogPost.setStatus(PostStatus.valueOf(postRequest.getStatus()));
            if (PostStatus.valueOf(postRequest.getStatus()) == PostStatus.SCHEDULED && postRequest.getPublishAt() != null) {
                blogPost.setPublishAt(Instant.parse(postRequest.getPublishAt()));
            } else if (PostStatus.valueOf(postRequest.getStatus()) == PostStatus.PUBLISHED) {
                blogPost.setPublishAt(Instant.now());
            } else if (PostStatus.valueOf(postRequest.getStatus()) == PostStatus.DRAFT) {
                blogPost.setPublishAt(null);
            }
        } else {
            if (postRequest.getPublishAt() != null) {
                Instant publishAt = Instant.parse(postRequest.getPublishAt());
                if (publishAt.isAfter(Instant.now())) {
                    blogPost.setStatus(PostStatus.SCHEDULED);
                    blogPost.setPublishAt(publishAt);
                    // blogPost.setTimeCapsule(true); // Set the flag here
                } else {
                    blogPost.setStatus(PostStatus.PUBLISHED);
                    blogPost.setPublishAt(Instant.now());
                    // blogPost.setTimeCapsule(false);
                }
            } else {
                blogPost.setStatus(PostStatus.PUBLISHED);
                blogPost.setPublishAt(Instant.now());
                // blogPost.setTimeCapsule(false);
            }
        }

        return blogPostRepository.save(blogPost);
    }

    @Override
    public BlogPost updatePost(String id, PostRequest postRequest, UserDetails userDetails) {
        User user = getUserByEmail(userDetails.getUsername());
        BlogPost blogPost = getPostById(id, userDetails);

        if (!blogPost.getAuthorId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to update this post.");
        }

        blogPost.setTitle(postRequest.getTitle());
        blogPost.setContent(postRequest.getContent());
        blogPost.setPrivate(postRequest.isPrivate());
        // blogPost.setFileUrl(postRequest.getFileUrl()); // REMOVED

        // Handle status updates
        if (postRequest.getStatus() != null) {
            blogPost.setStatus(PostStatus.valueOf(postRequest.getStatus()));
            if (PostStatus.valueOf(postRequest.getStatus()) == PostStatus.SCHEDULED && postRequest.getPublishAt() != null) {
                blogPost.setPublishAt(Instant.parse(postRequest.getPublishAt()));
            } else if (PostStatus.valueOf(postRequest.getStatus()) == PostStatus.PUBLISHED) {
                blogPost.setPublishAt(Instant.now());
            } else if (PostStatus.valueOf(postRequest.getStatus()) == PostStatus.DRAFT) {
                blogPost.setPublishAt(null);
            }
        } else {
            if (postRequest.getPublishAt() != null) {
                Instant publishAt = Instant.parse(postRequest.getPublishAt());
                if (publishAt.isAfter(Instant.now())) {
                    blogPost.setStatus(PostStatus.SCHEDULED);
                    blogPost.setPublishAt(publishAt);
                } else {
                    blogPost.setStatus(PostStatus.PUBLISHED);
                    blogPost.setPublishAt(Instant.now());
                }
            }
        }

        return blogPostRepository.save(blogPost);
    }

    // ... (rest of the methods are unchanged)
    
    @Override
    public void deletePost(String id, UserDetails userDetails) {
        User user = getUserByEmail(userDetails.getUsername());
        BlogPost blogPost = getPostById(id, userDetails);

        if (!blogPost.getAuthorId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to delete this post.");
        }

        commentRepository.deleteByPostId(id);
        
        blogPostRepository.delete(blogPost);
    }

    @Override
    public BlogPost toggleLike(String postId, UserDetails userDetails) {
        User user = getUserByEmail(userDetails.getUsername());
        BlogPost blogPost = getPostById(postId, userDetails);

        if (blogPost.getLikedBy() == null) {
            blogPost.setLikedBy(new HashSet<>());
        }

        if (blogPost.getLikedBy().contains(user.getUsername())) {
            blogPost.getLikedBy().remove(user.getUsername());
            blogPost.setLikesCount(blogPost.getLikesCount() - 1);
        } else {
            blogPost.getLikedBy().add(user.getUsername());
            blogPost.setLikesCount(blogPost.getLikesCount() + 1);
        }

        return blogPostRepository.save(blogPost);
    }

    @Override
    public CommentResponse addComment(String postId, CommentRequest commentRequest, UserDetails userDetails) {
        User user = getUserByEmail(userDetails.getUsername());
        BlogPost blogPost = getPostById(postId, userDetails);

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(user.getId());
        comment.setAuthorUsername(user.getUsername());
        comment.setContent(commentRequest.getContent());

        Comment savedComment = commentRepository.save(comment);

        blogPost.setCommentsCount(blogPost.getCommentsCount() + 1);
        blogPostRepository.save(blogPost);

        return convertToCommentResponse(savedComment);
    }

    @Override
    public List<CommentResponse> getComments(String postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        return comments.stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(String commentId, UserDetails userDetails) {
        User user = getUserByEmail(userDetails.getUsername());
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getAuthorId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to delete this comment.");
        }

        BlogPost blogPost = blogPostRepository.findById(comment.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", comment.getPostId()));
        blogPost.setCommentsCount(blogPost.getCommentsCount() - 1);
        blogPostRepository.save(blogPost);

        commentRepository.delete(comment);
    }
    
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private CommentResponse convertToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPostId());
        response.setAuthorId(comment.getAuthorId());
        response.setAuthorUsername(comment.getAuthorUsername());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}