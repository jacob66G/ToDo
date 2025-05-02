package com.example.ToDo.controller;

import com.example.ToDo.constant.ApplicationConstants;
import com.example.ToDo.dto.UserLoginDto;
import com.example.ToDo.dto.UserLoginResponseDto;
import com.example.ToDo.dto.UserRegistrationDto;
import com.example.ToDo.dto.UserResponseDto;
import com.example.ToDo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {

        UserResponseDto savedUserResponse = this.userService.registerUser(userRegistrationDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(ApplicationConstants.LOCATION_HEADER, "/api/users/" + savedUserResponse.getId())
                .body(savedUserResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> authenticate(@RequestBody UserLoginDto userLoginDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.userService.authenticateUser(userLoginDto));
    }
}
