package com.example.ToDo.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private int Status;
    private String Message;
    private String details;
    private LocalDateTime timestamp;
}
