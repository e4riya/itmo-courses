package ru.itmo.onlinecourses.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.CourseRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.CourseResponse;
import ru.itmo.onlinecourses.service.CourseService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public List<CourseResponse> all() {
        return courseService.findAll();
    }

    @GetMapping("/{id}")
    public CourseResponse one(@PathVariable UUID id) {
        return courseService.findById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<CourseResponse> byCategory(@PathVariable UUID categoryId) {
        return courseService.findByCategory(categoryId);
    }

    @PostMapping
    public CourseResponse create(@Valid @RequestBody CourseRequest request) {
        return courseService.create(request);
    }

    @PutMapping("/{id}")
    public CourseResponse update(@PathVariable UUID id, @Valid @RequestBody CourseRequest request) {
        return courseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        courseService.delete(id);
    }

    @PostMapping("/{courseId}/publish")
    public CourseResponse publish(@PathVariable UUID courseId) {
        return courseService.publish(courseId);
    }
}
