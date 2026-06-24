package ru.itmo.onlinecourses.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.EnrollmentResponse;
import ru.itmo.onlinecourses.service.EnrollmentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping("/api/courses/{courseId}/enroll")
    public EnrollmentResponse enroll(@PathVariable UUID courseId, @RequestParam UUID studentId) {
        return enrollmentService.enroll(courseId, studentId);
    }

    @GetMapping("/api/students/{studentId}/enrollments")
    public List<EnrollmentResponse> byStudent(@PathVariable UUID studentId) {
        return enrollmentService.findByStudent(studentId);
    }

    @GetMapping("/api/courses/{courseId}/enrollments")
    public List<EnrollmentResponse> byCourse(@PathVariable UUID courseId) {
        return enrollmentService.findByCourse(courseId);
    }
}
