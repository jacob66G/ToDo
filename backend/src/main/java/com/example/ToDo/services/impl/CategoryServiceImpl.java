package com.example.ToDo.services.impl;

import com.example.ToDo.exceptions.DuplicateNameException;
import com.example.ToDo.exceptions.HasAssociatedTasksException;
import com.example.ToDo.exceptions.ResourceNotFoundException;
import com.example.ToDo.mapper.CategoryMapper;
import com.example.ToDo.models.Category;
import com.example.ToDo.models.User;
import com.example.ToDo.dto.CategoryDto;
import com.example.ToDo.dto.CategoryResponseDto;
import com.example.ToDo.repositories.CategoryRepository;
import com.example.ToDo.repositories.TaskRepository;
import com.example.ToDo.services.CategoryService;
import com.example.ToDo.services.UserService;
import jakarta.validation.Valid;
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
    private final TaskRepository taskRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper,
            UserService userService,
            TaskRepository taskRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<CategoryResponseDto> getCategoriesDtoByUser() {
        return categoryRepository.findAllByUser_Username(getCurrentUserName()).stream()
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
    public CategoryResponseDto createCategory(@Valid CategoryDto categoryDto) {
        String username = getCurrentUserName();
        checkForDuplicateName(username, categoryDto.name());

        User user = userService.getUserByUsername(username);

        Category category = new Category();
        category.setName(categoryDto.name());

        user.addCategory(category);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, @Valid CategoryDto categoryDto) {
        Category category = getCategoryById(id);
        checkForDuplicateName(getCurrentUserName(), categoryDto.name());

        category.setName(categoryDto.name());

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategoryById(Long id) {
        Category category = getCategoryById(id);
        checkForConnectionsWithTasks(getCurrentUserName(), id);

        categoryRepository.delete(category);
    }

    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private void checkForDuplicateName(String username, String name) {
        if (categoryRepository.existsByUser_UsernameAndName(username, name)) {
            throw new DuplicateNameException(Category.class.getSimpleName(), name);
        }
    }

    private void checkForConnectionsWithTasks(String username, Long categoryId) {
        if(taskRepository.existsByUser_UsernameAndCategory_Id(username, categoryId)) {
            throw new HasAssociatedTasksException(Category.class.getSimpleName(), categoryId);
        }
    }
}
