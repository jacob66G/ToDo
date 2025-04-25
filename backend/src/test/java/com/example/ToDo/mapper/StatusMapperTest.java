package com.example.ToDo.mapper;

import com.example.ToDo.dto.StatusResponseDto;
import com.example.ToDo.models.DefaultStatus;
import com.example.ToDo.models.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class StatusMapperTest {

    @Autowired
    private StatusMapper statusMapper;

    @Test
    void toDto_shouldReturnStatusResponseDto() {
        //given
        Long id = 1L;
        String name = DefaultStatus.NEW.name();
        boolean isDefault = false;

        Status status = new Status();
        status.setId(id);
        status.setName(name);
        status.setDefault(isDefault);

        StatusResponseDto expectedResponse = new StatusResponseDto(id, name, isDefault);

        //when
        StatusResponseDto result = statusMapper.toDto(status);

        //then
        assertEquals(expectedResponse, result);
    }
}