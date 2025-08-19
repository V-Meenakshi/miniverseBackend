package com.example.chronoblog.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
// import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@Data
@NoArgsConstructor
// @CrossOrigin(origins = "http://localhost:5173")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    private Set<String> roles = new HashSet<>();

    private String fullName;
    private String bio;
    private String profileImageUrl;

    // --- NEW FIELDS ---
    private AccountStatus accountStatus;
    
    @CreatedDate
    private Instant createdAt; // This will be the "Joined Date"

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}