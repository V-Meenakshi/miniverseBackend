package com.example.chronoblog.repository;

import com.example.chronoblog.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * Repository interface for the User model.
 * By extending MongoRepository, we get a full set of CRUD (Create, Read, Update, Delete)
 * operations for the User collection without writing any implementation code.
 */
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by their email address.
     * Spring Data MongoDB automatically creates the query based on the method name.
     *
     * @param email The email of the user to find.
     * @return An Optional containing the user if found, or an empty Optional if not.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email.
     * This is more efficient than fetching the whole user object if you only need to check for existence.
     *
     * @param email The email to check.
     * @return true if a user with the email exists, false otherwise.
     */
    Boolean existsByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     *
     * @param username The username to check.
     * @return true if a user with the username exists, false otherwise.
     */
    Boolean existsByUsername(String username);
    Optional<User> findByUsername(String username); // <-- ADD THIS LINE

}