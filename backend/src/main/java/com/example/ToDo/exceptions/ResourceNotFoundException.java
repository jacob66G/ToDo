package com.example.ToDo.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " with id " + id + " not found");
    }

    public ResourceNotFoundException(String resourceName, String name) {
        super(resourceName + " with name '" + name + "' not found");
    }
}
