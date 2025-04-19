package com.example.ToDo.mapper;

import com.example.ToDo.models.Status;
import com.example.ToDo.models.dtos.StatusResponseDto;
import org.springframework.stereotype.Component;

@Component
public class StatusMapper {

    public StatusResponseDto toDto(Status status) {
        return new StatusResponseDto(
                status.getId(),
                status.getName(),
                status.isDefault()
        );
    }
}
