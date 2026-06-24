package ru.itmo.onlinecourses.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.CertificateResponse;
import ru.itmo.onlinecourses.service.CertificateService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CertificateController {
    private final CertificateService certificateService;

    @GetMapping("/api/students/{studentId}/certificates")
    public List<CertificateResponse> byStudent(@PathVariable UUID studentId) {
        return certificateService.findByStudent(studentId);
    }

    @GetMapping("/api/courses/{courseId}/certificates")
    public List<CertificateResponse> byCourse(@PathVariable UUID courseId) {
        return certificateService.findByCourse(courseId);
    }
}
