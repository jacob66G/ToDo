package com.example.ToDo.services.impl;

import com.example.ToDo.constant.ApplicationConstants;
import com.example.ToDo.dto.UserRegistrationDto;
import com.example.ToDo.dto.UserResponseDto;
import com.example.ToDo.exceptions.ResourceNotFoundException;
import com.example.ToDo.exceptions.UserAlreadyExistsException;
import com.example.ToDo.mapper.UserMapper;
import com.example.ToDo.models.Category;
import com.example.ToDo.models.DefaultStatus;
import com.example.ToDo.models.Status;
import com.example.ToDo.models.User;
import com.example.ToDo.repositories.UserRepository;
import com.example.ToDo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName(), username));
    }

    @Override
    public UserResponseDto registerUser(UserRegistrationDto userRegistrationDto) {

        // throwing exception if user with given username and email already exists
        String username = userRegistrationDto.getUsername();
        Optional<User> optionalUserByUsername = userRepository.findByUsername(username);
        if(optionalUserByUsername.isPresent()) {
            throw new UserAlreadyExistsException(
                    "User with username: " + username + " already exists"
            );
        }

        String email = userRegistrationDto.getEmail();
        Optional<User> optionalUserByEmail = userRepository.findByEmail(email);
        if(optionalUserByEmail.isPresent()) {
            throw new UserAlreadyExistsException(
                    "User with email: " + email + " already exists"
            );
        }

        User user = UserMapper.fromDto(userRegistrationDto);

        // hashing user password before storing in database
        user.setPassword(this.passwordEncoder.encode(userRegistrationDto.getPassword()));

        // creating default category for user
        user.addCategory(new Category(ApplicationConstants.DEFAULT_CATEGORY));

        // adding default statuses for user
        Set<Status> statuses = Set.of(
                new Status(DefaultStatus.NEW.name(), true),
                new Status(DefaultStatus.IN_PROGRESS.name(), true),
                new Status(DefaultStatus.COMPLETED.name(), true));

        statuses.forEach(
                user::addStatus
        );

        // saving user to db
        User savedUser = userRepository.save(user);

        return UserMapper.toDto(savedUser);
    }


}
