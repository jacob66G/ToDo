package com.example.ToDo.services;

import com.example.ToDo.dto.StatusDto;
import com.example.ToDo.dto.StatusResponseDto;
import com.example.ToDo.exceptions.DuplicateNameException;
import com.example.ToDo.exceptions.HasAssociatedTasksException;
import com.example.ToDo.exceptions.ResourceNotFoundException;
import com.example.ToDo.mapper.StatusMapper;
import com.example.ToDo.models.Status;
import com.example.ToDo.models.User;
import com.example.ToDo.repositories.StatusRepository;
import com.example.ToDo.services.impl.StatusServiceImpl;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusServiceImplTest {

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private StatusMapper statusMapper;

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @InjectMocks
    private StatusServiceImpl statusService;

    private String testUsername = "user";

    private void setUpSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
    }

    @Test
    void getAllStatusDtoByUser_shouldReturnListOfStatusDto_whenStatusesExistForUser() {
        //given
        setUpSecurityContext();

        Status status1 = new Status();
        status1.setId(1L);
        status1.setName("Test");
        status1.setDefault(false);

        Status status2 = new Status();
        status2.setId(2L);
        status2.setDefault(false);
        status2.setName("Test2");

        List<Status> statuses = List.of(status1, status2);

        StatusResponseDto statusResponseDto1 = new StatusResponseDto(1L, "Test", false);
        StatusResponseDto statusResponseDto2 = new StatusResponseDto(2L, "Test2", false);
        List<StatusResponseDto> expectedResponse = List.of(statusResponseDto1, statusResponseDto2);

        when(statusRepository.findAllByUser_Username(testUsername)).thenReturn(statuses);
        when(statusMapper.toDto(status1)).thenReturn(statusResponseDto1);
        when(statusMapper.toDto(status2)).thenReturn(statusResponseDto2);

        //when
        List<StatusResponseDto> result = statusService.getAllStatusDtoByUser();

        //then
        assertEquals(expectedResponse, result);

        verify(statusRepository, times(1)).findAllByUser_Username(testUsername);
    }

    @Test
    void getAllStatusDtoByUser_shouldReturnEmptyList_whenNoStatusesExistForUser() {
        //given
        setUpSecurityContext();

        //when
        List<StatusResponseDto> result = statusService.getAllStatusDtoByUser();

        //then
        assertEquals(Collections.emptyList(), result);

        verify(statusRepository, times(1)).findAllByUser_Username(testUsername);
    }

    @Test
    void getStatusDtoById_shouldReturnStatusResponseDto_whenStatusExist() {
        //given
        Long statusId = 1L;

        Status status = new Status();
        status.setId(statusId);
        status.setName("Test");
        status.setDefault(false);

        StatusResponseDto expectedResponse = new StatusResponseDto(statusId, "Test", false);

        when(statusRepository.findById(statusId)).thenReturn(Optional.of(status));
        when(statusMapper.toDto(status)).thenReturn(expectedResponse);

        //when
        StatusResponseDto result = statusService.getStatusDtoById(statusId);

        //then
        assertEquals(expectedResponse, result);

        verify(statusRepository, times(1)).findById(statusId);
        verify(statusMapper, times(1)).toDto(status);
    }

    @Test
    void getStatusDtoById_shouldThrowResourceNotFoundException_whenStatusDoesNotExist() {
        //given
        Long statusId = 1L;
        when(statusRepository.findById(statusId)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> statusService.getStatusDtoById(statusId));

        verify(statusRepository, times(1)).findById(statusId);
    }

    @Test
    void getStatusById_shouldReturnStatus_whenStatusExists() {
        //given
        Long statusId = 1L;
        Status expectedResponse = new Status();
        expectedResponse.setId(statusId);
        expectedResponse.setName("Test");
        expectedResponse.setDefault(false);

        when(statusRepository.findById(statusId)).thenReturn(Optional.of(expectedResponse));

        //when
        Status result = statusService.getStatusById(statusId);

        //then
        assertEquals(expectedResponse, result);

        verify(statusRepository, times(1)).findById(statusId);
    }

    @Test
    void getStatusById_shouldThrowResourceNotFoundException_whenStatusDoesNotExist() {
        //given
        Long statusId = 1L;
        when(statusRepository.findById(statusId)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> statusService.getStatusById(statusId));

        verify(statusRepository, times(1)).findById(statusId);
    }

    @Test
    void createStatus_shouldCreateStatusAndReturnStatusResponseDto() {
        //given
        User user = new User();
        user.setUsername(testUsername);

        setUpSecurityContext();

        String statusName = "Test";
        StatusDto statusDto = new StatusDto(statusName);

        Status savedStatus = new Status();
        savedStatus.setId(1L);
        savedStatus.setName(statusName);
        savedStatus.setDefault(false);
        savedStatus.setUser(user);

        StatusResponseDto expectedResponse = new StatusResponseDto(1L, "Test", false);

        when(statusRepository.existsByUser_UsernameAndName(testUsername, statusName)).thenReturn(false);
        when(userService.getUserByUsername(testUsername)).thenReturn(user);
        when(statusRepository.save(any(Status.class))).thenReturn(savedStatus);
        when(statusMapper.toDto(savedStatus)).thenReturn(expectedResponse);

        //when
        StatusResponseDto result = statusService.createStatus(statusDto);

        //then
        assertEquals(expectedResponse, result);
        verify(statusRepository, times(1)).existsByUser_UsernameAndName(testUsername, statusName);
        verify(userService, times(1)).getUserByUsername(testUsername);
        verify(statusRepository, times(1)).save(any(Status.class));
        verify(statusMapper, times(1)).toDto(savedStatus);
    }

    @Test
    void createStatus_shouldThrowDuplicateNameException_whenNameAlreadyExistsForUser() {
        //given
        setUpSecurityContext();

        String statusName = "test";
        when(statusRepository.existsByUser_UsernameAndName(testUsername, statusName)).thenReturn(true);

        //when + then
        assertThrows(DuplicateNameException.class, () -> statusService.createStatus(new StatusDto(statusName)));

        verify(statusRepository, times(1)).existsByUser_UsernameAndName(testUsername, statusName);
    }

    @Test
    void updateStatus_shouldUpdateStatusAndReturnStatusResponseDto() {
        //given
        setUpSecurityContext();

        Long statusId = 1L;
        Status status = new Status();
        status.setId(statusId);
        status.setName("Test");
        status.setDefault(false);

        String newStatusName = "newTest";
        StatusDto statusDto = new StatusDto(newStatusName);

        Status savedStatus = new Status();
        savedStatus.setId(statusId);
        savedStatus.setName(newStatusName);
        savedStatus.setDefault(false);

        StatusResponseDto expectedResponse = new StatusResponseDto(statusId, newStatusName, false);

        when(statusRepository.findById(statusId)).thenReturn(Optional.of(status));
        when(statusRepository.existsByUser_UsernameAndName(testUsername, newStatusName)).thenReturn(false);
        when(statusRepository.save(any(Status.class))).thenReturn(savedStatus);
        when(statusMapper.toDto(savedStatus)).thenReturn(expectedResponse);

        //when
        StatusResponseDto result = statusService.updateStatus(statusId, statusDto);

        //then
        assertEquals(expectedResponse, result);

        verify(statusRepository, times(1)).findById(statusId);
        verify(statusRepository, times(1)).existsByUser_UsernameAndName(testUsername, newStatusName);
        verify(statusRepository, times(1)).save(any(Status.class));
        verify(statusMapper, times(1)).toDto(savedStatus);
    }

    @Test
    void updateStatus_shouldThrowResourceNotFoundException_whenStatusDoesNotExist() {
        //given
        Long statusId = 1L;
        when(statusRepository.findById(statusId)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> statusService.updateStatus(statusId, new StatusDto("Test")));

        verify(statusRepository, times(1)).findById(statusId);
        verify(statusRepository, never()).save(any(Status.class));
    }

    @Test
    void updateStatus_shouldThrowDuplicateNameException_whenNameAlreadyExistsForUser() {
        //given
        setUpSecurityContext();

        Long statusId = 1L;
        String statusName = "test";
        StatusDto statusDto = new StatusDto(statusName);

        Status savedStatus = new Status();
        savedStatus.setId(statusId);
        savedStatus.setName(statusName);

        when(statusRepository.findById(statusId)).thenReturn(Optional.of(savedStatus));
        when(statusRepository.existsByUser_UsernameAndName(testUsername, statusName)).thenReturn(true);

        //when + then
        assertThrows(DuplicateNameException.class, () -> statusService.updateStatus(statusId, statusDto));

        verify(statusRepository, times(1)).existsByUser_UsernameAndName(testUsername, statusName);
        verify(statusRepository, never()).save(any(Status.class));
    }

    @Test
    void deleteStatusById_shouldDeleteStatus() {
        //given
        setUpSecurityContext();

        Long statusId = 1L;
        Status savedStatus = new Status();
        savedStatus.setId(statusId);

        when(statusRepository.findById(statusId)).thenReturn(Optional.of(savedStatus));
        doNothing().when(taskService).checkIfStatusHasAssociatedTasks(testUsername, statusId);

        //when
        statusService.deleteStatusById(statusId);

        //then
        verify(statusRepository, times(1)).findById(statusId);
        verify(taskService, times(1)).checkIfStatusHasAssociatedTasks(testUsername, statusId);
        verify(statusRepository, times(1)).delete(savedStatus);
    }

    @Test
    void deleteStatusById_shouldThrowResourceNotFoundException_whenStatusDoesNotExist() {
        //given
        Long statusId = 1L;
        when(statusRepository.findById(statusId)).thenReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> statusService.deleteStatusById(statusId));
    }

    @Test
    void deleteStatusById_shouldThrownHasAssociatedTasksException_whenStatusIsAssociatedToTask() {
        //given
        setUpSecurityContext();
        Long statusId = 1L;
        Status savedStatus = new Status();
        savedStatus.setId(statusId);

        when(statusRepository.findById(statusId)).thenReturn(Optional.of(savedStatus));
        doThrow(new HasAssociatedTasksException(Status.class.getSimpleName(), statusId))
                .when(taskService).checkIfStatusHasAssociatedTasks(testUsername, statusId);

        //when + then
        assertThrows(HasAssociatedTasksException.class, () -> statusService.deleteStatusById(statusId));
        verify(statusRepository, times(1)).findById(statusId);
        verify(taskService, times(1)).checkIfStatusHasAssociatedTasks(testUsername, statusId);
        verify(statusRepository, never()).delete(savedStatus);
    }
}