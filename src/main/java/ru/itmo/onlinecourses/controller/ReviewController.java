package ru.itmo.onlinecourses.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.ReviewRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.ReviewResponse;
import ru.itmo.onlinecourses.service.ReviewService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/api/courses/{courseId}/reviews")
    public ReviewResponse create(@PathVariable UUID courseId, @Valid @RequestBody ReviewRequest request) {
        return reviewService.create(courseId, request);
    }

    @GetMapping("/api/courses/{courseId}/reviews")
    public List<ReviewResponse> byCourse(@PathVariable UUID courseId) {
        return reviewService.findByCourse(courseId);
    }

    @GetMapping("/api/courses/{courseId}/reviews/average-rating")
    public Map<String, Object> average(@PathVariable UUID courseId) {
        return Map.of("courseId", courseId, "averageRating", reviewService.averageRating(courseId));
    }
}
