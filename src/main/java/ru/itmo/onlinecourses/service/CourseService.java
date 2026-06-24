package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.onlinecourses.dto.ApiDtos.CourseRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.CourseResponse;
import ru.itmo.onlinecourses.entity.Category;
import ru.itmo.onlinecourses.entity.Course;
import ru.itmo.onlinecourses.enums.CourseStatus;
import ru.itmo.onlinecourses.exception.NotFoundException;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.repository.CategoryRepository;
import ru.itmo.onlinecourses.repository.CourseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final EntityMapper mapper;

    @Transactional(readOnly = true)
    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream().map(mapper::toCourseResponse).toList();
    }

    @Transactional(readOnly = true)
    public CourseResponse findById(UUID id) {
        return mapper.toCourseResponse(getCourse(id));
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> findByCategory(UUID categoryId) {
        return courseRepository.findByCategoryId(categoryId).stream().map(mapper::toCourseResponse).toList();
    }

    @Transactional
    public CourseResponse create(CourseRequest request) {
        Course course = new Course();
        apply(course, request);
        return mapper.toCourseResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse update(UUID id, CourseRequest request) {
        Course course = getCourse(id);
        apply(course, request);
        return mapper.toCourseResponse(courseRepository.save(course));
    }

    @Transactional
    public void delete(UUID id) {
        courseRepository.delete(getCourse(id));
    }

    @Transactional
    public CourseResponse publish(UUID courseId) {
        Course course = getCourse(courseId);
        course.setStatus(CourseStatus.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        return mapper.toCourseResponse(courseRepository.save(course));
    }

    public Course getCourse(UUID id) {
        return courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found: " + id));
    }

    private void apply(Course course, CourseRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found: " + request.categoryId()));
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setPrice(request.price());
        course.setDifficultyLevel(request.difficultyLevel());
        course.setStatus(request.status());
        course.setCategory(category);
        course.setInstructor(userService.getUser(request.instructorId()));
    }
}
