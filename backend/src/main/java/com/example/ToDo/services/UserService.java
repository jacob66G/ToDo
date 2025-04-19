package com.example.ToDo.services;

import com.example.ToDo.models.User;

public interface UserService {
    User getUserByUsername(String username);
}
