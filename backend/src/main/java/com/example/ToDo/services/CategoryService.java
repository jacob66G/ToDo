package com.example.ToDo.services;

import com.example.ToDo.models.Category;
import com.example.ToDo.models.dtos.CategoryDto;
import com.example.ToDo.models.dtos.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> getCategoriesDtoByUser();
    CategoryResponseDto getCategoryDtoById(Long id);
    Category getCategoryById(Long id);
    CategoryResponseDto createCategory(CategoryDto categoryDto);
    CategoryResponseDto updateCategory(Long id, CategoryDto categoryDto);
    void deleteCategoryById(Long id);
}
