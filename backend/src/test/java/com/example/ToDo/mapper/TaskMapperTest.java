package com.example.ToDo.mapper;

import com.example.ToDo.dto.TaskResponseDto;
import com.example.ToDo.models.Category;
import com.example.ToDo.models.Status;
import com.example.ToDo.models.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TaskMapperTest {

    @Autowired
    private TaskMapper mapper;

    @Test
    void toDto_shouldReturnTaskResponseDto() {
        //given
        Long id = 1L;
        String title = "title";
        String description = "description";
        String categoryName = "categoryName";
        String statusName = "statusName";

        Category category = new Category();
        category.setName(categoryName);

        Status status = new Status();
        status.setName(statusName);

        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setCategory(category);
        task.setStatus(status);

        TaskResponseDto expectedResponse = new TaskResponseDto(id, title, description, categoryName, statusName);

        //when
        TaskResponseDto result = mapper.toDto(task);

        //then
        assertEquals(expectedResponse, result);

    }
}