package com.example.ToDo.services.impl;

import com.example.ToDo.dto.TaskUpdateDto;
import com.example.ToDo.exceptions.DuplicateNameException;
import com.example.ToDo.exceptions.HasAssociatedTasksException;
import com.example.ToDo.exceptions.ResourceNotFoundException;
import com.example.ToDo.mapper.TaskMapper;
import com.example.ToDo.models.*;
import com.example.ToDo.dto.TaskResponseDto;
import com.example.ToDo.dto.TaskCreateDto;
import com.example.ToDo.repositories.TaskRepository;
import com.example.ToDo.services.CategoryService;
import com.example.ToDo.services.StatusService;
import com.example.ToDo.services.TaskService;
import com.example.ToDo.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final CategoryService categoryService;
    private final StatusService statusService;
    private final UserService userService;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            TaskMapper taskMapper,
            CategoryService categoryService,
            StatusService statusService,
            UserService userService
    ) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.categoryService = categoryService;
        this.statusService = statusService;
        this.userService = userService;
    }

    @Override
    public List<TaskResponseDto> getTasksDtoByUser() {
        return taskRepository.findAllByUser(getCurrentUserName()).stream()
                .map(taskMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public TaskResponseDto getTaskDtoById(Long id) {
        return taskRepository.findById(id).map(taskMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(Task.class.getSimpleName(), id));
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Task.class.getSimpleName(), id));
    }

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskCreateDto taskCreateDto) {
        String username = getCurrentUserName();
        checkForDuplicateTitle(username, taskCreateDto.title(), null);

        User user = userService.getUserByEmail(username);

        Task task = Task.builder()
                .title(taskCreateDto.title())
                .description(taskCreateDto.description())
                .category(categoryService.getCategoryById(taskCreateDto.categoryId()))
                .status(statusService.getStatusByName(DefaultStatus.NEW.name()))
                .build();

        user.addTask(task);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long id, TaskUpdateDto taskDto) {
        Task task = getTaskById(id);
        checkForDuplicateTitle(getCurrentUserName(), taskDto.title(), id);

        task.setTitle(taskDto.title());
        task.setDescription(taskDto.description());

        Category category = categoryService.getCategoryById(taskDto.categoryId());
        task.setCategory(category);

        Status status = statusService.getStatusById(taskDto.statusId());
        task.setStatus(status);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    @Override
    public void checkIfCategoryHasAssociatedTasks(String username, Long categoryId) {
        if (!taskRepository.findByUserAndCategory_Id(username, categoryId).isEmpty()) {
            throw new HasAssociatedTasksException(Category.class.getSimpleName(), categoryId);
        }
    }

    @Override
    public void checkIfStatusHasAssociatedTasks(String username, Long statusId) {
        if (!taskRepository.findByUserAndStatus_Id(username, statusId).isEmpty()) {
            throw new HasAssociatedTasksException(Status.class.getSimpleName(), statusId);
        }
    }

    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private void checkForDuplicateTitle(String username, String title, Long taskId) {
        if (!taskRepository.findByUserAndTitle(username, title, taskId).isEmpty()) {
            throw new DuplicateNameException(Task.class.getSimpleName(), title);
        }
    }
}
