package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.onlinecourses.dto.ApiDtos.LessonRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.LessonResponse;
import ru.itmo.onlinecourses.dto.ApiDtos.EnrollmentResponse;
import ru.itmo.onlinecourses.entity.Enrollment;
import ru.itmo.onlinecourses.entity.Lesson;
import ru.itmo.onlinecourses.entity.LessonProgress;
import ru.itmo.onlinecourses.exception.BadRequestException;
import ru.itmo.onlinecourses.exception.NotFoundException;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.repository.LessonProgressRepository;
import ru.itmo.onlinecourses.repository.LessonRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final ModuleService moduleService;
    private final EnrollmentService enrollmentService;
    private final EntityMapper mapper;

    @Transactional(readOnly = true)
    public List<LessonResponse> findByModule(UUID moduleId) {
        return lessonRepository.findByModuleIdOrderByPosition(moduleId).stream().map(mapper::toLessonResponse).toList();
    }

    @Transactional
    public LessonResponse create(UUID moduleId, LessonRequest request) {
        Lesson lesson = new Lesson();
        lesson.setModule(moduleService.getModule(moduleId));
        apply(lesson, request);
        return mapper.toLessonResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonResponse update(UUID lessonId, LessonRequest request) {
        Lesson lesson = getLesson(lessonId);
        apply(lesson, request);
        return mapper.toLessonResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public void delete(UUID lessonId) {
        lessonRepository.delete(getLesson(lessonId));
    }

    @Transactional
    public EnrollmentResponse complete(UUID lessonId, UUID studentId) {
        Lesson lesson = getLesson(lessonId);
        UUID courseId = lesson.getModule().getCourse().getId();
        Enrollment enrollment = enrollmentService.findEntityByStudentAndCourse(studentId, courseId);
        lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
                .ifPresent(progress -> {
                    throw new BadRequestException("Lesson already completed");
                });
        LessonProgress progress = new LessonProgress();
        progress.setEnrollment(enrollment);
        progress.setLesson(lesson);
        lessonProgressRepository.saveAndFlush(progress);
        return enrollmentService.findById(enrollment.getId());
    }

    public Lesson getLesson(UUID id) {
        return lessonRepository.findById(id).orElseThrow(() -> new NotFoundException("Lesson not found: " + id));
    }

    private void apply(Lesson lesson, LessonRequest request) {
        lesson.setTitle(request.title());
        lesson.setContentUrl(request.contentUrl());
        lesson.setDurationMinutes(request.durationMinutes());
        lesson.setPosition(request.position());
        lesson.setFreePreview(request.freePreview());
    }
}
