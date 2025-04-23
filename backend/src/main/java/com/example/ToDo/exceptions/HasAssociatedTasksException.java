package com.example.ToDo.exceptions;

public class HasAssociatedTasksException extends RuntimeException {
    public HasAssociatedTasksException(String resourceName, Long resourceId) {
        super(resourceName + " with id " + resourceId + " has associated tasks");
    }
}
