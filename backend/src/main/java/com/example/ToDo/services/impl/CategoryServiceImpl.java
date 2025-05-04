package com.example.ToDo.services.impl;

import com.example.ToDo.exceptions.DuplicateNameException;
import com.example.ToDo.exceptions.ResourceNotFoundException;
import com.example.ToDo.mapper.CategoryMapper;
import com.example.ToDo.models.Category;
import com.example.ToDo.models.User;
import com.example.ToDo.dto.CategoryDto;
import com.example.ToDo.dto.CategoryResponseDto;
import com.example.ToDo.repositories.CategoryRepository;
import com.example.ToDo.services.CategoryService;
import com.example.ToDo.services.TaskService;
import com.example.ToDo.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserService userService;
    private final TaskService taskService;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper,
            UserService userService,
            @Lazy TaskService taskService
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.userService = userService;
        this.taskService = taskService;
    }

    @Override
    public List<CategoryResponseDto> getCategoriesDtoByUser() {
        return categoryRepository.findAllByUser(getCurrentUser()).stream()
                .map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto getCategoryDtoById(Long id) {
        return categoryRepository.findById(id).map(categoryMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(Category.class.getSimpleName(), id));
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Category.class.getSimpleName(), id));
    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryDto categoryDto) {
        String username = getCurrentUser();
        checkForDuplicateName(username, categoryDto.name());

        User user = userService.getUserByEmail(username);

        Category category = new Category();
        category.setName(categoryDto.name());

        user.addCategory(category);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = getCategoryById(id);
        checkForDuplicateName(getCurrentUser(), categoryDto.name());

        category.setName(categoryDto.name());

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {
        Category category = getCategoryById(id);
        taskService.checkIfCategoryHasAssociatedTasks(getCurrentUser(), category.getId());

        categoryRepository.delete(category);
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private void checkForDuplicateName(String username, String name) {
        if (categoryRepository.existsByUserAndName(username, name)) {
            throw new DuplicateNameException(Category.class.getSimpleName(), name);
        }
    }

}
