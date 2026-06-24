package ru.itmo.onlinecourses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.onlinecourses.entity.Submission;

import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findByStudentId(UUID studentId);
}
