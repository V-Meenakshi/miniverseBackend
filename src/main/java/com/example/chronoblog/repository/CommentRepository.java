package com.example.chronoblog.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.chronoblog.model.Comment;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    List<Comment> findByPostIdOrderByCreatedAtDesc(String postId);
    
    long countByPostId(String postId);
    
    void deleteByPostId(String postId);
}
