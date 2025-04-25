package com.example.ToDo.mapper;

import com.example.ToDo.dto.CategoryResponseDto;
import com.example.ToDo.models.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryMapperTest {

    @Autowired
    private CategoryMapper mapper;

    @Test
    void toDto_shouldReturnCategoryResponseDto() {
        //given
        Long categoryId = 1L;
        String categoryName = "Test";

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        CategoryResponseDto expected = new CategoryResponseDto(categoryId, categoryName);

        //when
        CategoryResponseDto result = mapper.toDto(category);

        //then
        assertEquals(expected, result);
    }

}