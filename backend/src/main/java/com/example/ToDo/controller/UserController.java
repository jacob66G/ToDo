package com.example.ToDo.controller;

import com.example.ToDo.constant.ApplicationConstants;
import com.example.ToDo.dto.UserRegistrationDto;
import com.example.ToDo.dto.UserResponseDto;
import com.example.ToDo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/users", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {

        UserResponseDto savedUserResponse = this.userService.registerUser(userRegistrationDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(ApplicationConstants.LOCATION_HEADER, "/api/users" + savedUserResponse.getId())
                .body(savedUserResponse);
    }
}
