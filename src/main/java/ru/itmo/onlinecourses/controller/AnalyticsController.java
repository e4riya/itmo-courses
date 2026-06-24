package ru.itmo.onlinecourses.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.service.AnalyticsService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/top-courses")
    public List<Map<String, Object>> topCourses() {
        return analyticsService.topCourses();
    }

    @GetMapping("/course-average-progress")
    public List<Map<String, Object>> courseAverageProgress() {
        return analyticsService.courseAverageProgress();
    }

    @GetMapping("/student-average-grades")
    public List<Map<String, Object>> studentAverageGrades() {
        return analyticsService.studentAverageGrades();
    }

    @GetMapping("/instructor-income")
    public List<Map<String, Object>> instructorIncome() {
        return analyticsService.instructorIncome();
    }

    @GetMapping("/student-ranking")
    public List<Map<String, Object>> studentRanking() {
        return analyticsService.studentRanking();
    }

    @GetMapping("/courses-with-progress-above")
    public List<Map<String, Object>> coursesWithProgressAbove(@RequestParam BigDecimal value) {
        return analyticsService.coursesWithProgressAbove(value);
    }

    @GetMapping("/inactive-students")
    public List<Map<String, Object>> inactiveStudents() {
        return analyticsService.inactiveStudents();
    }

    @GetMapping("/category-statistics")
    public List<Map<String, Object>> categoryStatistics() {
        return analyticsService.categoryStatistics();
    }

    @GetMapping("/courses-without-payments")
    public List<Map<String, Object>> coursesWithoutPayments() {
        return analyticsService.coursesWithoutPayments();
    }

    @GetMapping("/monthly-income")
    public List<Map<String, Object>> monthlyIncome() {
        return analyticsService.monthlyIncome();
    }
}
