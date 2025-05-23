package com.example.ToDo.services.impl;

import com.example.ToDo.constant.ApplicationConstants;
import com.example.ToDo.dto.UserLoginDto;
import com.example.ToDo.dto.UserLoginResponseDto;
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
import com.example.ToDo.security.service.JwtService;
import com.example.ToDo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName(), email));
    }

    @Override
    public UserResponseDto registerUser(UserRegistrationDto userRegistrationDto) {

        // throwing exception if user with given username and email already exists
        String username = userRegistrationDto.getUsername();
        Optional<User> optionalUserByUsername = userRepository.findByUserEmail(username);
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

    /**
     * @return
     */
    @Override
    public UserLoginResponseDto authenticateUser(UserLoginDto userLoginDto) {

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.unauthenticated(userLoginDto.getEmail(), userLoginDto.getPassword());

        try {
             authentication = (UsernamePasswordAuthenticationToken) this.authenticationManager.authenticate(authentication);
        } catch (AuthenticationException ex) {
            log.info("Bad credentials provided");
            throw new BadCredentialsException("Bad Credentials");
        }

        String jwtToken = jwtService.generateToken(authentication);

        return new UserLoginResponseDto(jwtToken);
    }


}
