package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.onlinecourses.dto.ApiDtos.EnrollmentResponse;
import ru.itmo.onlinecourses.entity.Enrollment;
import ru.itmo.onlinecourses.enums.EnrollmentStatus;
import ru.itmo.onlinecourses.exception.NotFoundException;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.repository.EnrollmentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final EntityMapper mapper;

    @Transactional
    public EnrollmentResponse enroll(UUID courseId, UUID studentId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).orElseGet(() -> {
            Enrollment created = new Enrollment();
            created.setStudent(userService.getUser(studentId));
            created.setCourse(courseService.getCourse(courseId));
            created.setProgressPercent(BigDecimal.ZERO);
            created.setStatus(EnrollmentStatus.ACTIVE);
            return created;
        });
        return mapper.toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse findById(UUID id) {
        return mapper.toEnrollmentResponse(getEnrollment(id));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findByStudent(UUID studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream().map(mapper::toEnrollmentResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findByCourse(UUID courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream().map(mapper::toEnrollmentResponse).toList();
    }

    public Enrollment findEntityByStudentAndCourse(UUID studentId, UUID courseId) {
        return enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found for student " + studentId + " and course " + courseId));
    }

    private Enrollment getEnrollment(UUID id) {
        return enrollmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Enrollment not found: " + id));
    }
}
