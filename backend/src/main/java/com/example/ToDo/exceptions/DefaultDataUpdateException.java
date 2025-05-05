package com.example.ToDo.exceptions;

public class DefaultDataUpdateException extends RuntimeException {
    public DefaultDataUpdateException(String resource, String name) {
        super("Could not update " + resource + " with name " + name + " because it is default data");
    }
}
