package com.example.ToDo.exceptions;

public class DefaultDataDeletionException extends RuntimeException {
    public DefaultDataDeletionException(String resource, String name) {
        super("Could not delete " + resource + " with name " + name + " because it is default data");
    }
}
