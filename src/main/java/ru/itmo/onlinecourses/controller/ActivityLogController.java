package ru.itmo.onlinecourses.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.ActivityLogRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.ActivityLogResponse;
import ru.itmo.onlinecourses.service.ActivityLogService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ActivityLogController {
    private final ActivityLogService activityLogService;

    @PostMapping("/api/activity-logs")
    public ActivityLogResponse create(@Valid @RequestBody ActivityLogRequest request) {
        return activityLogService.create(request);
    }

    @GetMapping("/api/users/{userId}/activity-logs")
    public List<ActivityLogResponse> byUser(@PathVariable UUID userId) {
        return activityLogService.findByUser(userId);
    }
}
