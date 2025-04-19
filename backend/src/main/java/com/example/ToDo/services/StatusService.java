package com.example.ToDo.services;

import com.example.ToDo.models.Status;
import com.example.ToDo.models.dtos.StatusDto;
import com.example.ToDo.models.dtos.StatusResponseDto;

import java.util.List;

public interface StatusService {
    List<StatusResponseDto> getAllStatusDtoByUser();
    StatusResponseDto getStatusDtoById(Long id);
    Status getStatusById(Long id);
    Status getStatusByName(String name);
    StatusResponseDto createStatus(StatusDto statusDto);
    StatusResponseDto updateStatus(Long id, StatusDto statusDto);
    void deleteStatusById(Long id);
}
