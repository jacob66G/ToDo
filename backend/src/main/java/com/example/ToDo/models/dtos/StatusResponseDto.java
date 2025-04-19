package com.example.ToDo.models.dtos;

public record StatusResponseDto(
        Long id,
        String name,
        Boolean isDefault
) {}
