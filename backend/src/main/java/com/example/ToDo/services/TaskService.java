package com.example.ToDo.services;

import com.example.ToDo.dto.TaskUpdateDto;
import com.example.ToDo.models.Task;
import com.example.ToDo.dto.TaskResponseDto;
import com.example.ToDo.dto.TaskCreateDto;

import java.util.List;

public interface TaskService {
    List<TaskResponseDto> getTasksDtoByUser();
    TaskResponseDto getTaskDtoById(Long id);
    Task getTaskById(Long id);
    TaskResponseDto createTask(TaskCreateDto taskCreateDto);
    TaskResponseDto updateTask(Long id, TaskUpdateDto taskDto);
    void deleteTaskById(Long id);
    void checkIfCategoryHasAssociatedTasks(String username, Long categoryId);
    void checkIfStatusHasAssociatedTasks(String username, Long statusId);
}
