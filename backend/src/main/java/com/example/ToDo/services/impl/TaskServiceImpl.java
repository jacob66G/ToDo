package com.example.ToDo.services.impl;

import com.example.ToDo.exceptions.DuplicateNameException;
import com.example.ToDo.exceptions.ResourceNotFoundException;
import com.example.ToDo.mapper.TaskMapper;
import com.example.ToDo.models.*;
import com.example.ToDo.dto.TaskResponseDto;
import com.example.ToDo.dto.TaskDto;
import com.example.ToDo.repositories.TaskRepository;
import com.example.ToDo.services.CategoryService;
import com.example.ToDo.services.StatusService;
import com.example.ToDo.services.TaskService;
import com.example.ToDo.services.UserService;
import jakarta.validation.Valid;
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
        return taskRepository.findAllByUser_Username(getCurrentUserName()).stream()
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
    public TaskResponseDto createTask(@Valid TaskDto taskDto) {
        String username = getCurrentUserName();
        checkForDuplicateTitle(username, taskDto.title());

        User user = userService.getUserByUsername(username);

        Task task = new Task();
        task.setTitle(taskDto.title());
        task.setDescription(taskDto.description());
        task.setCategory(categoryService.getCategoryById(taskDto.categoryId()));
        task.setStatus(statusService.getStatusByName(String.valueOf(DefaultStatus.NEW)));

        user.addTask(task);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskResponseDto updateTask(Long id, @Valid TaskDto taskDto) {
        Task task = getTaskById(id);
        checkForDuplicateTitle(getCurrentUserName(), taskDto.title());

        task.setTitle(taskDto.title());
        task.setDescription(taskDto.description());

        Category category = categoryService.getCategoryById(taskDto.categoryId());
        task.setCategory(category);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskResponseDto updateStatus(Long taskId, Long statusId) {
        Task task = getTaskById(taskId);
        Status status = statusService.getStatusById(statusId);

        task.setStatus(status);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public void deleteTaskById(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private void checkForDuplicateTitle(String username, String title) {
        if (taskRepository.existsByUser_UsernameAndTitle(username, title)) {
            throw new DuplicateNameException(Task.class.getSimpleName(), title);
        }
    }
}
