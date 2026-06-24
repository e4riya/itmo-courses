package ru.itmo.onlinecourses.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.AssignmentRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.AssignmentResponse;
import ru.itmo.onlinecourses.service.AssignmentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @GetMapping("/api/lessons/{lessonId}/assignments")
    public List<AssignmentResponse> byLesson(@PathVariable UUID lessonId) {
        return assignmentService.findByLesson(lessonId);
    }

    @PostMapping("/api/lessons/{lessonId}/assignments")
    public AssignmentResponse create(@PathVariable UUID lessonId, @Valid @RequestBody AssignmentRequest request) {
        return assignmentService.create(lessonId, request);
    }
}
