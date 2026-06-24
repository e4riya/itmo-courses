package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.onlinecourses.dto.ApiDtos.SubmissionRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.SubmissionResponse;
import ru.itmo.onlinecourses.entity.Submission;
import ru.itmo.onlinecourses.enums.SubmissionStatus;
import ru.itmo.onlinecourses.exception.NotFoundException;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.repository.SubmissionRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final AssignmentService assignmentService;
    private final UserService userService;
    private final EntityMapper mapper;

    @Transactional
    public SubmissionResponse submit(UUID assignmentId, UUID studentId, SubmissionRequest request) {
        Submission submission = new Submission();
        submission.setAssignment(assignmentService.getAssignment(assignmentId));
        submission.setStudent(userService.getUser(studentId));
        submission.setAnswerText(request.answerText());
        submission.setStatus(SubmissionStatus.SUBMITTED);
        return mapper.toSubmissionResponse(submissionRepository.save(submission));
    }

    @Transactional(readOnly = true)
    public SubmissionResponse findById(UUID id) {
        return mapper.toSubmissionResponse(getSubmission(id));
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> findByStudent(UUID studentId) {
        return submissionRepository.findByStudentId(studentId).stream().map(mapper::toSubmissionResponse).toList();
    }

    public Submission getSubmission(UUID id) {
        return submissionRepository.findById(id).orElseThrow(() -> new NotFoundException("Submission not found: " + id));
    }
}
