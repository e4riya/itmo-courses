package ru.itmo.onlinecourses.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.ModuleRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.ModuleResponse;
import ru.itmo.onlinecourses.service.ModuleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;

    @GetMapping("/api/courses/{courseId}/modules")
    public List<ModuleResponse> byCourse(@PathVariable UUID courseId) {
        return moduleService.findByCourse(courseId);
    }

    @PostMapping("/api/courses/{courseId}/modules")
    public ModuleResponse create(@PathVariable UUID courseId, @Valid @RequestBody ModuleRequest request) {
        return moduleService.create(courseId, request);
    }

    @PutMapping("/api/modules/{moduleId}")
    public ModuleResponse update(@PathVariable UUID moduleId, @Valid @RequestBody ModuleRequest request) {
        return moduleService.update(moduleId, request);
    }

    @DeleteMapping("/api/modules/{moduleId}")
    public void delete(@PathVariable UUID moduleId) {
        moduleService.delete(moduleId);
    }
}
