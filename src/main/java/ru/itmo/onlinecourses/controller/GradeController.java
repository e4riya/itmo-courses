package ru.itmo.onlinecourses.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.GradeRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.GradeResponse;
import ru.itmo.onlinecourses.service.GradeService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GradeController {
    private final GradeService gradeService;

    @PostMapping("/api/submissions/{submissionId}/grade")
    public GradeResponse grade(@PathVariable UUID submissionId, @RequestParam UUID graderId,
                               @Valid @RequestBody GradeRequest request) {
        return gradeService.grade(submissionId, graderId, request);
    }

    @GetMapping("/api/submissions/{submissionId}/grade")
    public GradeResponse bySubmission(@PathVariable UUID submissionId) {
        return gradeService.findBySubmission(submissionId);
    }
}
