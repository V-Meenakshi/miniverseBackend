package com.example.chronoblog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for cases where a requested resource is not found in the database.
 * The @ResponseStatus annotation causes Spring Boot to automatically respond with the
 * specified HTTP status code (404 NOT_FOUND) whenever this exception is thrown
 * from a controller.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException.
     *
     * @param resourceName the name of the resource that was not found (e.g., "BlogPost").
     * @param fieldName the name of the field used for the lookup (e.g., "id").
     * @param fieldValue the value of the field that was searched for.
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}