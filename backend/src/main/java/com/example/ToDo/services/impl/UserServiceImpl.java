package com.example.ToDo.services.impl;

import com.example.ToDo.exceptions.ResourceNotFoundException;
import com.example.ToDo.models.User;
import com.example.ToDo.repositories.UserRepository;
import com.example.ToDo.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName(), username));
    }
}
