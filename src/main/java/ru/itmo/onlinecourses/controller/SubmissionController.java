package ru.itmo.onlinecourses.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.SubmissionRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.SubmissionResponse;
import ru.itmo.onlinecourses.service.SubmissionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping("/api/assignments/{assignmentId}/submit")
    public SubmissionResponse submit(@PathVariable UUID assignmentId, @RequestParam UUID studentId,
                                     @Valid @RequestBody SubmissionRequest request) {
        return submissionService.submit(assignmentId, studentId, request);
    }

    @GetMapping("/api/submissions/{id}")
    public SubmissionResponse one(@PathVariable UUID id) {
        return submissionService.findById(id);
    }

    @GetMapping("/api/students/{studentId}/submissions")
    public List<SubmissionResponse> byStudent(@PathVariable UUID studentId) {
        return submissionService.findByStudent(studentId);
    }
}
