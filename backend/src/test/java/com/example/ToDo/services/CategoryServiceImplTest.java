package com.example.ToDo.services;

import com.example.ToDo.dto.CategoryDto;
import com.example.ToDo.dto.CategoryResponseDto;
import com.example.ToDo.exceptions.DuplicateNameException;
import com.example.ToDo.exceptions.HasAssociatedTasksException;
import com.example.ToDo.exceptions.ResourceNotFoundException;
import com.example.ToDo.mapper.CategoryMapper;
import com.example.ToDo.models.Category;
import com.example.ToDo.models.User;
import com.example.ToDo.repositories.CategoryRepository;
import com.example.ToDo.services.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    CategoryMapper categoryMapper;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    TaskService taskService;

    @Mock
    UserService userService;

    @InjectMocks
    CategoryServiceImpl categoryService;

    private String testUsername = "user";


    private void setUpSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
    }

    @Test
    void getCategoriesDtoByUser_ShouldReturnListOfCategoryResponseDtos_WhenCategoriesExistForUser() {
        //given
        setUpSecurityContext();

        Category category1= new Category();
        category1.setId(1L);
        category1.setName("category 1");

        Category category2= new Category();
        category2.setId(2L);
        category2.setName("category 2");

        List<Category> categories = List.of(category1,category2);

        CategoryResponseDto responseDto1 = new CategoryResponseDto(1L, "Category 1");
        CategoryResponseDto responseDto2 = new CategoryResponseDto(2L, "Category 2");
        List<CategoryResponseDto> expectedResponse = List.of(responseDto1, responseDto2);

        when(categoryRepository.findAllByUser(testUsername)).thenReturn(categories);
        when(categoryMapper.toDto(category1)).thenReturn(responseDto1);
        when(categoryMapper.toDto(category2)).thenReturn(responseDto2);

        //when
        List<CategoryResponseDto> result = categoryService.getCategoriesDtoByUser();

        //then
        assertEquals(expectedResponse, result);

        verify(categoryRepository, times(1)).findAllByUser(testUsername);
        verify(categoryMapper, times(1)).toDto(category1);
        verify(categoryMapper, times(1)).toDto(category2);
    }

    @Test
    void getCategoriesDtoByUser_shouldReturnEmptyList_whenNoCategoriesExistForUser() {
        //when
        setUpSecurityContext();

        //when
        List<CategoryResponseDto> result = categoryService.getCategoriesDtoByUser();

        //then
        assertEquals(Collections.emptyList(), result);

        verify(categoryRepository, times(1)).findAllByUser(testUsername);
    }

    @Test
    void getCategoryDtoById_shouldReturnCategoryResponseDto_whenCategoryExists() {
        //given
        Long categoryId = 1L;
        String name = "category 1";
        Category category = new Category();
        category.setId(categoryId);
        category.setName(name);

        CategoryResponseDto expected = new CategoryResponseDto(categoryId, name);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(new CategoryResponseDto(categoryId, name));

        //when
        CategoryResponseDto result = categoryService.getCategoryDtoById(categoryId);

        //then
        assertEquals(expected, result);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    void getCategoryDtoById_shouldThrowResourceNotFoundException_whenCategoryDoesNotExist() {
        //given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //when + then
       assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryDtoById(categoryId));

       verify(categoryRepository, times(1)).findById(categoryId);
       verify(categoryMapper, never()).toDto(any(Category.class));
    }

    @Test
    void getCategoryById_shouldReturnCategory_whenCategoryExists() {
        //given
        Long categoryId = 1L;
        String name = "category 1";
        Category expected = new Category();
        expected.setId(categoryId);
        expected.setName(name);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expected));

        //when
        Category result = categoryService.getCategoryById(categoryId);

        //then
        assertEquals(expected, result);

        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void getCategoryById_shouldThrowResourceNotFoundException_whenCategoryDoesNotExist() {
        //given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(categoryId));

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, never()).toDto(any(Category.class));
    }

    @Test
    void createCategory_shouldCreateCategoryAndReturnCategoryResponseDto() {
        //given
        User user = new User();
        user.setUsername(testUsername);

        setUpSecurityContext();

        String categoryName = "category1";
        CategoryDto categoryDto = new CategoryDto(categoryName);

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName(categoryName);
        savedCategory.setUser(user);
        CategoryResponseDto expectedResponse = new CategoryResponseDto(1L, categoryName);

        when(categoryRepository.existsByUserAndName(testUsername, categoryName)).thenReturn(false);
        when(userService.getUserByEmail(testUsername)).thenReturn(user);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(expectedResponse);

        //when
        CategoryResponseDto result = categoryService.createCategory(categoryDto);

        //then
        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(categoryRepository, times(1)).existsByUserAndName(testUsername, categoryName);
        verify(userService, times(1)).getUserByEmail(testUsername);
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryMapper, times(1)).toDto(savedCategory);
    }

    @Test
    void createCategory_shouldThrowDuplicateNameException_whenNameAlreadyExistsForUser() {
        //given
        String categoryName = "category1";
        when(categoryRepository.existsByUserAndName(testUsername, categoryName)).thenReturn(true);

        //when + then
        assertThrows(DuplicateNameException.class, () -> categoryService.createCategory(new CategoryDto(categoryName)));

        verify(categoryRepository, times(1)).existsByUserAndName(testUsername, categoryName);
        verify(userService, never()).getUserByEmail(testUsername);
        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryMapper, never()).toDto(any(Category.class));
    }


    @Test
    void updateCategory_shouldUpdateCategoryAndReturnCategoryResponseDto() {
        //given
        setUpSecurityContext();

        Long categoryId = 1L;
        String categoryName = "category1";
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        String newCategoryName = "newCategoryName";
        CategoryDto updatedCategoryDto = new CategoryDto(newCategoryName);

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName(newCategoryName);

        CategoryResponseDto expectedResponse = new CategoryResponseDto(1L, newCategoryName);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByUserAndName(testUsername, newCategoryName)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(expectedResponse);

        //when
        CategoryResponseDto result = categoryService.updateCategory(categoryId, updatedCategoryDto);

        //then
        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).existsByUserAndName(testUsername, newCategoryName);
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryMapper, times(1)).toDto(any(Category.class));
    }

    @Test
    void updateCategory_shouldThrowResourceNotFoundException_whenCategoryDoesNotExist() {
        //given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(categoryId, new CategoryDto("newCategoryName")));
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void updateCategory_shouldThrowDuplicateNameException_whenNameAlreadyExistsForUser() {
        //given
        setUpSecurityContext();

        Long categoryId = 1L;
        String categoryName = "category1";
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByUserAndName(testUsername, categoryName)).thenReturn(true);

        //when + then
        assertThrows(DuplicateNameException.class, () -> categoryService.updateCategory(categoryId, new CategoryDto(categoryName)));

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).existsByUserAndName(testUsername, categoryName);
    }


    @Test
    void deleteCategoryById_shouldDeleteCategory() {
        //given
        setUpSecurityContext();

        Long categoryId = 1L;
        String categoryName = "category1";
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);


        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(taskService).checkIfCategoryHasAssociatedTasks(testUsername, categoryId);

        //when
        categoryService.deleteCategoryById(categoryId);

        //then
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(taskService, times(1)).checkIfCategoryHasAssociatedTasks(testUsername, categoryId);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategoryById_shouldThrowResourceNotFoundException_whenCategoryDoesNotExist() {
        //given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategoryById(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void deleteCategoryById_shouldThrowHasAssociatedTasksException_whenCategoryIsAssociatedToTask() {
        //given
        setUpSecurityContext();

        Long categoryId = 1L;
        String categoryName = "category1";
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doThrow(new HasAssociatedTasksException(Category.class.getSimpleName(), categoryId))
                .when(taskService).checkIfCategoryHasAssociatedTasks(testUsername, categoryId);

        //when + then
        assertThrows(HasAssociatedTasksException.class, () -> categoryService.deleteCategoryById(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(taskService, times(1)).checkIfCategoryHasAssociatedTasks(testUsername, categoryId);
    }
}