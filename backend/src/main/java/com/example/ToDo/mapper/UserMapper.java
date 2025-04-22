package com.example.ToDo.mapper;

import com.example.ToDo.dto.UserRegistrationDto;
import com.example.ToDo.dto.UserResponseDto;
import com.example.ToDo.models.User;

public class UserMapper {

    private UserMapper(){}

    public static UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public static User fromDto(UserRegistrationDto userRegistrationDto) {
        return new User(
                userRegistrationDto.getUsername(),
                userRegistrationDto.getEmail()
        );
    }
}
