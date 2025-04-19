package com.example.ToDo.mapper;

import com.example.ToDo.models.Task;
import com.example.ToDo.models.dtos.TaskResponseDto;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponseDto toDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCategory().getName(),
                task.getStatus().getName()
        );
    }
}
