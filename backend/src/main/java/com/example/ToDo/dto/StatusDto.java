package com.example.ToDo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StatusDto(
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 5, max = 20, message = "Status name should consist of a minimum of 5 to 30 characters")
        @Pattern(regexp = "^[a-zA-Z0-9 ,.:;!?()\\-+*/&^%$#@]*$", message = "Status name contains invalid characters")
        String name
) {}
