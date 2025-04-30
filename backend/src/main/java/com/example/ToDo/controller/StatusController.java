package com.example.ToDo.controller;

import com.example.ToDo.dto.StatusDto;
import com.example.ToDo.dto.StatusResponseDto;
import com.example.ToDo.services.StatusService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    public ResponseEntity<List<StatusResponseDto>> getStatusList() {
        List<StatusResponseDto> response =  statusService.getAllStatusDtoByUser();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusResponseDto> getStatusById(@PathVariable("id") Long id) {
        StatusResponseDto response = statusService.getStatusDtoById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<StatusResponseDto> createStatus(@RequestBody @Valid StatusDto status) {
        StatusResponseDto response = statusService.createStatus(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusResponseDto> updateStatus(@PathVariable("id") Long id, @RequestBody @Valid StatusDto status) {
        StatusResponseDto response = statusService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable("id") Long id) {
        statusService.deleteStatusById(id);
        return ResponseEntity.noContent().build();
    }
}
