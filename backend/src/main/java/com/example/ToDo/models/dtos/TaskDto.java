package com.example.ToDo.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TaskDto(
        @NotBlank(message = "Title cannot be empty")
        @Size(min = 5, max = 30, message = "Title should consist of a minimum of 5 to 30 characters")
        @Pattern(regexp = "^[a-zA-Z0-9 ,.:;!?()\\-+*/&^%$#@]*$", message = "Title contains invalid characters")
        String title,

        @NotBlank(message = "Description cannot be empty")
        @Size(min = 30, max = 255, message = "Description should consist of a minimum of 5 to 30 characters")
        @Pattern(regexp = "^[a-zA-Z0-9 ,.:;!?()\\-+*/&^%$#@]*$", message = "Description contains invalid characters")
        String description,

        @NotNull(message = "No category selected")
        Long categoryId
) {}
