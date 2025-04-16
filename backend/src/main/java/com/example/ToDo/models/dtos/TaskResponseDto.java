package com.example.ToDo.models.dtos;

public record TaskResponseDto(
        Long id,
        String title,
        String description,
        String categoryName,
        String statusName
) {}
