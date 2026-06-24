package ru.itmo.onlinecourses.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.EnrollmentResponse;
import ru.itmo.onlinecourses.dto.ApiDtos.LessonRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.LessonResponse;
import ru.itmo.onlinecourses.service.LessonService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/api/modules/{moduleId}/lessons")
    public List<LessonResponse> byModule(@PathVariable UUID moduleId) {
        return lessonService.findByModule(moduleId);
    }

    @PostMapping("/api/modules/{moduleId}/lessons")
    public LessonResponse create(@PathVariable UUID moduleId, @Valid @RequestBody LessonRequest request) {
        return lessonService.create(moduleId, request);
    }

    @PutMapping("/api/lessons/{lessonId}")
    public LessonResponse update(@PathVariable UUID lessonId, @Valid @RequestBody LessonRequest request) {
        return lessonService.update(lessonId, request);
    }

    @DeleteMapping("/api/lessons/{lessonId}")
    public void delete(@PathVariable UUID lessonId) {
        lessonService.delete(lessonId);
    }

    @PostMapping("/api/lessons/{lessonId}/complete")
    public EnrollmentResponse complete(@PathVariable UUID lessonId, @RequestParam UUID studentId) {
        return lessonService.complete(lessonId, studentId);
    }
}
