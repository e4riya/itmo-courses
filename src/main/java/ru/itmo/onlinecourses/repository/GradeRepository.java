package ru.itmo.onlinecourses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.onlinecourses.entity.Grade;

import java.util.Optional;
import java.util.UUID;

public interface GradeRepository extends JpaRepository<Grade, UUID> {
    Optional<Grade> findBySubmissionId(UUID submissionId);
}
