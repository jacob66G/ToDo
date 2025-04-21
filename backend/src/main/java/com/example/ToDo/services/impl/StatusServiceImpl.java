package com.example.ToDo.services.impl;

import com.example.ToDo.exceptions.DuplicateNameException;
import com.example.ToDo.exceptions.ResourceNotFoundException;
import com.example.ToDo.mapper.StatusMapper;
import com.example.ToDo.models.Category;
import com.example.ToDo.models.Status;
import com.example.ToDo.models.User;
import com.example.ToDo.dto.StatusDto;
import com.example.ToDo.dto.StatusResponseDto;
import com.example.ToDo.repositories.StatusRepository;
import com.example.ToDo.services.StatusService;
import com.example.ToDo.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;
    private final StatusMapper statusMapper;
    private final UserService userService;

    public StatusServiceImpl(StatusRepository statusRepository, StatusMapper statusMapper, UserService userService) {
        this.statusRepository = statusRepository;
        this.statusMapper = statusMapper;
        this.userService = userService;
    }

    @Override
    public List<StatusResponseDto> getAllStatusDtoByUser() {
        return statusRepository.findAllByUser_Username(getCurrentUserName()).stream()
                .map(statusMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public StatusResponseDto getStatusDtoById(Long id) {
        return statusRepository.findById(id).map(statusMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(Status.class.getSimpleName(), id));
    }

    @Override
    public Status getStatusById(Long id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Status.class.getSimpleName(), id));
    }

    @Override
    public Status getStatusByName(String name) {
        return statusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(Status.class.getSimpleName(), name));
    }

    @Override
    public StatusResponseDto createStatus(StatusDto statusDto) {
        String username = getCurrentUserName();
        checkForDuplicateName(statusDto.name(), username);

        User user = userService.getUserByUsername(username);

        Status status = new Status();
        status.setName(statusDto.name());
        status.setDefault(false);

        user.addStatus(status);

        return statusMapper.toDto(statusRepository.save(status));
    }

    @Override
    public StatusResponseDto updateStatus(Long id, StatusDto statusDto) {
        Status status = getStatusById(id);
        checkForDuplicateName(statusDto.name(), getCurrentUserName());

        status.setName(statusDto.name());

        return statusMapper.toDto(statusRepository.save(status));
    }

    @Override
    public void deleteStatusById(Long id) {
        Status status = getStatusById(id);
        statusRepository.delete(status);
    }

    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private void checkForDuplicateName(String name, String username) {
        if (statusRepository.existsByNameAndUser_Username(name, username)) {
            throw new DuplicateNameException(Category.class.getSimpleName(), name);
        }
    }
}
