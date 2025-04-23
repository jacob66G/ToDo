package com.example.ToDo.services;

import com.example.ToDo.models.Task;
import com.example.ToDo.dto.TaskResponseDto;
import com.example.ToDo.dto.TaskDto;

import java.util.List;

public interface TaskService {
    List<TaskResponseDto> getTasksDtoByUser();
    TaskResponseDto getTaskDtoById(Long id);
    Task getTaskById(Long id);
    TaskResponseDto createTask(TaskDto taskDto);
    TaskResponseDto updateTask(Long id, TaskDto taskDto);
    TaskResponseDto updateStatus(Long taskId, Long statusId);
    void deleteTaskById(Long id);
    void checkIfCategoryHasAssociatedTasks(String username, Long categoryId);
    void checkIfStatusHasAssociatedTasks(String username, Long statusId);
}
