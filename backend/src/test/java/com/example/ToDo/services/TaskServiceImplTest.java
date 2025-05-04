package com.example.ToDo.services;

import com.example.ToDo.dto.TaskDto;
import com.example.ToDo.dto.TaskResponseDto;
import com.example.ToDo.exceptions.DuplicateNameException;
import com.example.ToDo.exceptions.HasAssociatedTasksException;
import com.example.ToDo.exceptions.ResourceNotFoundException;
import com.example.ToDo.mapper.TaskMapper;
import com.example.ToDo.models.*;
import com.example.ToDo.repositories.TaskRepository;
import com.example.ToDo.services.impl.TaskServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private CategoryService categoryService;

    @Mock
    private StatusService statusService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private String testUsername = "user";

    private void setUpSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
    }

    @Test
    void getTasksDtoByUser_shouldReturnListOfTasksDto_whenTasksExistForUser() {
        //given
        setUpSecurityContext();

        Long task1Id = 1L;
        String task1Title = "Task1";
        String task1Description = "Task1";
        String task1CategoryName = "Task1";
        String task1StatusName = "Task1";

        Long task2Id = 2L;
        String task2Title = "Task2";
        String task2Description = "Task2";
        String task2CategoryName = "Task2";
        String task2StatusName = "Task2";

        Task task1 = new Task();
        task1.setId(task1Id);
        task1.setTitle(task1Title);
        task1.setDescription(task1Description);

        Task task2 = new Task();
        task2.setId(task2Id);
        task2.setTitle(task2Title);
        task2.setDescription(task2Description);

        List<Task> tasks = List.of(task1, task2);

        TaskResponseDto taskResponseDto1 = new TaskResponseDto(task1Id, task1Title, task1Description, task1CategoryName, task1StatusName);
        TaskResponseDto taskResponseDto2 = new TaskResponseDto(task2Id, task2Title, task2Description, task2CategoryName, task2StatusName);

        List<TaskResponseDto> expectedResponse = List.of(taskResponseDto1, taskResponseDto2);

        when(taskRepository.findAllByUser_Username(testUsername)).thenReturn(tasks);
        when(taskMapper.toDto(task1)).thenReturn(taskResponseDto1);
        when(taskMapper.toDto(task2)).thenReturn(taskResponseDto2);

        //when
        List<TaskResponseDto> result = taskService.getTasksDtoByUser();

        //then
        assertEquals(expectedResponse, result);

        verify(taskRepository, times(1)).findAllByUser_Username(testUsername);
        verify(taskMapper, times(2)).toDto(any(Task.class));
    }

    @Test
    void getTasksDtoByUser_shouldReturnEmptyList_whenNoTasksExistForUser() {
        //given
        setUpSecurityContext();
        when(taskRepository.findAllByUser_Username(testUsername)).thenReturn(Collections.emptyList());

        //when
        List<TaskResponseDto> result = taskService.getTasksDtoByUser();

        //then
        assertEquals(Collections.emptyList(), result);

        verify(taskRepository, times(1)).findAllByUser_Username(testUsername);
    }

    @Test
    void getTaskDtoById_shouldReturnTaskDto_whenTasksExist() {
        //given
        Long id = 1L;
        String title = "Task1";
        String description = "Task1";
        String categoryName = "Task1";
        String statusName = "Task1";

        Task task1 = new Task();
        task1.setId(id);
        task1.setTitle(title);
        task1.setDescription(description);

        TaskResponseDto expectedResponse = new TaskResponseDto(id, title, description, categoryName, statusName);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task1));
        when(taskMapper.toDto(task1)).thenReturn(expectedResponse);

        //when
        TaskResponseDto result = taskService.getTaskDtoById(id);

        //then
        assertEquals(expectedResponse, result);

        verify(taskRepository, times(1)).findById(id);
        verify(taskMapper, times(1)).toDto(task1);
    }

    @Test
    void getTaskDtoById_shouldThrowResourceNotFoundException_whenTasksDoesNotExist() {
        //given
        Long task1Id = 1L;
        when(taskRepository.findById(task1Id)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskDtoById(task1Id));

        verify(taskRepository, times(1)).findById(task1Id);
        verify(taskMapper, never()).toDto(any());
    }

    @Test
    void getTaskById_shouldReturnTask_whenTaskExist() {
        //given
        Long id = 1L;
        String title = "Task1";
        String description = "Task1";

        Task expectedResponse = new Task();
        expectedResponse.setId(id);
        expectedResponse.setTitle(title);
        expectedResponse.setDescription(description);

        when(taskRepository.findById(id)).thenReturn(Optional.of(expectedResponse));

        //when
        Task result = taskService.getTaskById(id);

        //then
        assertEquals(expectedResponse, result);

        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void getTaskById_shouldThrowResourceNotFoundException_whenTasksDoesNotExist() {
        Long task1Id = 1L;
        when(taskRepository.findById(task1Id)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(task1Id));

        verify(taskRepository, times(1)).findById(task1Id);
    }

    @Test
    void createTask_shouldCreateTaskAndReturnTaskResponseDto() {
        //given
        User user = new User();
        user.setUsername(testUsername);

        setUpSecurityContext();

        Long id = 1L;
        String title = "Task1";
        String description = "Task1";
        String categoryName = "Task1";
        String statusName = DefaultStatus.NEW.name();
        Long categoryId = 1L;

        Category category = new Category();
        category.setId(categoryId);

        Status status = new Status();
        status.setName(statusName);

        TaskDto newTaskDto = new TaskDto(title, description, categoryId);
        Task newTask = new Task();
        newTask.setId(id);
        newTask.setTitle(title);
        newTask.setDescription(description);
        newTask.setCategory(category);
        newTask.setStatus(status);

        TaskResponseDto expectedResponse = new TaskResponseDto(id, title, description, categoryName, statusName);

        when(taskRepository.existsByUser_UsernameAndTitle(testUsername, title)).thenReturn(false);
        when(userService.getUserByEmail(testUsername)).thenReturn(user);
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(statusService.getStatusByName(statusName)).thenReturn(status);
        when(taskRepository.save(any(Task.class))).thenReturn(newTask);
        when(taskMapper.toDto(newTask)).thenReturn(expectedResponse);

        //when
        TaskResponseDto result = taskService.createTask(newTaskDto);

        //then
        assertEquals(expectedResponse, result);

        verify(taskRepository, times(1)).existsByUser_UsernameAndTitle(testUsername, title);
        verify(userService, times(1)).getUserByEmail(testUsername);
        verify(categoryService, times(1)).getCategoryById(categoryId);
        verify(statusService, times(1)).getStatusByName(statusName);
        verify(taskMapper, times(1)).toDto(newTask);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_shouldThrowDuplicateNameException_whenTaskWithSameNameAlreadyExists() {
        //given
        setUpSecurityContext();

        String title = "Task1";
        when(taskRepository.existsByUser_UsernameAndTitle(testUsername, title)).thenReturn(true);

        //when + then
        assertThrows(DuplicateNameException.class, ()-> taskService.createTask(new TaskDto(title, "test", 1L)));

        verify(taskRepository, times(1)).existsByUser_UsernameAndTitle(testUsername, title);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_shouldUpdateTaskAndReturnTaskResponseDto() {
        //given
        setUpSecurityContext();

        Long id = 1L;
        Task task = new Task();
        task.setId(id);
        task.setTitle("Task1");
        task.setDescription("Task1");

        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        String newTitle = "new title";
        String newDescription = "new description";

        TaskDto newTaskDto = new TaskDto(newTitle, newDescription, 1L);

        Task newTask = new Task();
        newTask.setId(id);
        newTask.setTitle(newTitle);
        newTask.setDescription(newDescription);

        TaskResponseDto expectedResponse = new TaskResponseDto(id, newTitle, newDescription, "categoryName", "statusName" );

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.existsByUser_UsernameAndTitle(testUsername, newTitle)).thenReturn(false);
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(taskRepository.save(any(Task.class))).thenReturn(newTask);
        when(taskMapper.toDto(newTask)).thenReturn(expectedResponse);

        //when
        TaskResponseDto result = taskService.updateTask(id, newTaskDto);

        //then
        assertEquals(expectedResponse, result);

        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).existsByUser_UsernameAndTitle(testUsername, task.getTitle());
        verify(categoryService, times(1)).getCategoryById(categoryId);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskMapper, times(1)).toDto(newTask);
    }

    @Test
    void updateTask_shouldThrowDuplicateNameException_whenTaskWithSameNameAlreadyExists() {
        //given
        setUpSecurityContext();

        Long id = 1L;
        String newTitle = "Task1";

        Task task = new Task();
        task.setId(id);
        task.setTitle("Task1");

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.existsByUser_UsernameAndTitle(testUsername, newTitle)).thenReturn(true);

        //when + then
        assertThrows(DuplicateNameException.class, () -> taskService.updateTask(id, new TaskDto(newTitle, "test", 1L)));

        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).existsByUser_UsernameAndTitle(testUsername, newTitle);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_shouldThrowResourceNotFoundException_whenTaskDoesNotExists() {
        //given
        Long id = 1L;

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(id, new TaskDto("test", "test", 1L)));

        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, never()).existsByUser_UsernameAndTitle(testUsername, "test");
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateStatus_shouldUpdateTaskStatusAndReturnTaskResponseDto() {
        //given
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Task 1");

        Long statusId = 1L;
        String statusName = DefaultStatus.IN_PROGRESS.name();
        Status status = new Status();
        status.setId(statusId);
        status.setName(statusName);

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle("Task 1");
        updatedTask.setStatus(status);

        TaskResponseDto expectedResponse = new TaskResponseDto(taskId, "Task 1", "Task1", "categoryName", statusName);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(statusService.getStatusById(statusId)).thenReturn(status);
        when(taskRepository.save(existingTask)).thenReturn(updatedTask);
        when(taskMapper.toDto(updatedTask)).thenReturn(expectedResponse);

        //when
        TaskResponseDto result = taskService.updateStatus(taskId, statusId);

        //then
        assertEquals(expectedResponse, result);

        verify(taskRepository, times(1)).findById(taskId);
        verify(statusService, times(1)).getStatusById(statusId);
        verify(taskRepository, times(1)).save(existingTask);
        verify(taskMapper, times(1)).toDto(updatedTask);

    }

    @Test
    void updateStatus_shouldThrownResourceNotFoundException_whenTaskDoesNotExists() {
        //given
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> taskService.updateStatus(taskId, 1L));

        verify(taskRepository, times(1)).findById(taskId);
        verify(statusService, never()).getStatusById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateStatus_shouldThrownResourceNotFoundException_whenStatusDoesNotExists() {
        //given
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);

        Long statusId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(statusService.getStatusById(statusId)).thenThrow(new ResourceNotFoundException(Status.class.getSimpleName(), statusId));

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> taskService.updateStatus(taskId, 1L));

        verify(taskRepository).findById(taskId);
        verify(statusService).getStatusById(statusId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTaskById_shouldDeleteTask() {
        //given
        Long taskId = 1L;

        Task existingTask = new Task();
        existingTask.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        //when
        taskService.deleteTaskById(taskId);

        //then
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(existingTask);
    }

    @Test
    void deleteTaskById_shouldThrownResourceNotFoundException_whenTaskDoesNotExists() {
        //given
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTaskById(taskId));

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).delete(any());
    }

    @Test
    void checkIfCategoryHasAssociatedTasks_shouldDoNothing_whenCategoryHasNotAssociatedTasks() {
        //given
        Long categoryId = 1L;

        when(taskRepository.existsByUser_UsernameAndCategory_Id(testUsername, categoryId)).thenReturn(false);

        // when + then
        assertDoesNotThrow(() -> taskService.checkIfCategoryHasAssociatedTasks(testUsername, categoryId));

        verify(taskRepository).existsByUser_UsernameAndCategory_Id(testUsername, categoryId);
    }

    @Test
    void checkIfCategoryHasAssociatedTasks_shouldThrowHasAssociatedTasksException_whenCategoryHasAssociatedTasks() {
        //given
        Long categoryId = 1L;

        when(taskRepository.existsByUser_UsernameAndCategory_Id(testUsername, categoryId)).thenReturn(true);

        //when + then
        assertThrows(HasAssociatedTasksException.class, () -> taskService.checkIfCategoryHasAssociatedTasks(testUsername, categoryId));

        verify(taskRepository).existsByUser_UsernameAndCategory_Id(testUsername, categoryId);
    }

    @Test
    void checkIfStatusHasAssociatedTasks_shouldDoNothing_whenStatusHasNotAssociatedTasks() {
        //given
        Long statusId = 1L;

        when(taskRepository.existsByUser_UsernameAndStatus_Id(testUsername, statusId)).thenReturn(false);

        // when + then
        assertDoesNotThrow(() -> taskService.checkIfStatusHasAssociatedTasks(testUsername, statusId));

        verify(taskRepository).existsByUser_UsernameAndStatus_Id(testUsername, statusId);
    }

    @Test
    void checkIfStatusHasAssociatedTasks_shouldThrowHasAssociatedTasksException_whenStatusHasAssociatedTasks() {
        //given
        Long statusId = 1L;

        when(taskRepository.existsByUser_UsernameAndStatus_Id(testUsername, statusId)).thenReturn(true);

        //when + then
        assertThrows(HasAssociatedTasksException.class, () -> taskService.checkIfStatusHasAssociatedTasks(testUsername, statusId));

        verify(taskRepository).existsByUser_UsernameAndStatus_Id(testUsername, statusId);
    }
}