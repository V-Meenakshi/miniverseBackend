package com.example.chronoblog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PostRequest {
    private String title;
    private String content;
    private String publishAt;
    private String status;
    private String fileUrl;
    
    @JsonProperty("isPrivate")
    private boolean isPrivate;
}