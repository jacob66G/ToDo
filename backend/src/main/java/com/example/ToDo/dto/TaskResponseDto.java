package com.example.ToDo.dto;

public record TaskResponseDto(
        Long id,
        String title,
        String description,
        String categoryName,
        String statusName
) {}
