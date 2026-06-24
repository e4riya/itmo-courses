package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.onlinecourses.dto.ApiDtos.AssignmentRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.AssignmentResponse;
import ru.itmo.onlinecourses.entity.Assignment;
import ru.itmo.onlinecourses.exception.NotFoundException;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.repository.AssignmentRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final LessonService lessonService;
    private final EntityMapper mapper;

    @Transactional(readOnly = true)
    public List<AssignmentResponse> findByLesson(UUID lessonId) {
        return assignmentRepository.findByLessonId(lessonId).stream().map(mapper::toAssignmentResponse).toList();
    }

    @Transactional
    public AssignmentResponse create(UUID lessonId, AssignmentRequest request) {
        Assignment assignment = new Assignment();
        assignment.setLesson(lessonService.getLesson(lessonId));
        assignment.setTitle(request.title());
        assignment.setDescription(request.description());
        assignment.setMaxScore(request.maxScore());
        assignment.setDeadlineDays(request.deadlineDays());
        return mapper.toAssignmentResponse(assignmentRepository.save(assignment));
    }

    public Assignment getAssignment(UUID id) {
        return assignmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Assignment not found: " + id));
    }
}
