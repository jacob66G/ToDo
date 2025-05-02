package com.example.ToDo.services;

import com.example.ToDo.dto.UserLoginDto;
import com.example.ToDo.dto.UserLoginResponseDto;
import com.example.ToDo.dto.UserRegistrationDto;
import com.example.ToDo.dto.UserResponseDto;
import com.example.ToDo.models.User;

public interface UserService {
    User getUserByUsername(String username);

    UserResponseDto registerUser(UserRegistrationDto userRegistrationDto);

    UserLoginResponseDto authenticateUser(UserLoginDto userLoginDto);
}
