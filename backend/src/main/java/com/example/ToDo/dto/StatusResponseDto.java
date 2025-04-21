package com.example.ToDo.dto;

public record StatusResponseDto(
        Long id,
        String name,
        Boolean isDefault
) {}
