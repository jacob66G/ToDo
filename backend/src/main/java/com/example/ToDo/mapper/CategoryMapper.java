package com.example.ToDo.mapper;

import com.example.ToDo.models.Category;
import com.example.ToDo.models.dtos.CategoryResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponseDto toDto(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName()
        );
    }
}
