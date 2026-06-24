package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.onlinecourses.dto.ApiDtos.GradeRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.GradeResponse;
import ru.itmo.onlinecourses.entity.Grade;
import ru.itmo.onlinecourses.entity.Submission;
import ru.itmo.onlinecourses.enums.SubmissionStatus;
import ru.itmo.onlinecourses.exception.NotFoundException;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.repository.GradeRepository;
import ru.itmo.onlinecourses.repository.SubmissionRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionService submissionService;
    private final UserService userService;
    private final EntityMapper mapper;

    @Transactional
    public GradeResponse grade(UUID submissionId, UUID graderId, GradeRequest request) {
        Submission submission = submissionService.getSubmission(submissionId);
        Grade grade = gradeRepository.findBySubmissionId(submissionId).orElseGet(Grade::new);
        grade.setSubmission(submission);
        grade.setGrader(userService.getUser(graderId));
        grade.setScore(request.score());
        grade.setFeedback(request.feedback());
        Grade saved = gradeRepository.saveAndFlush(grade);
        submission.setStatus(SubmissionStatus.CHECKED);
        submissionRepository.save(submission);
        return mapper.toGradeResponse(saved);
    }

    @Transactional(readOnly = true)
    public GradeResponse findBySubmission(UUID submissionId) {
        return mapper.toGradeResponse(gradeRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new NotFoundException("Grade not found for submission: " + submissionId)));
    }
}
