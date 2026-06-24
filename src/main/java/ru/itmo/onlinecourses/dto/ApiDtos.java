package ru.itmo.onlinecourses.dto;

import jakarta.validation.constraints.*;
import ru.itmo.onlinecourses.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record UserRequest(
            @Email @NotBlank String email,
            @NotBlank String passwordHash,
            @NotBlank String firstName,
            @NotBlank String lastName,
            Set<RoleName> roles
    ) {
    }

    public record UserResponse(UUID id, String email, String firstName, String lastName, boolean active,
                               Set<RoleName> roles, LocalDateTime createdAt) {
    }

    public record CourseRequest(
            @NotBlank String title,
            String description,
            @NotNull @PositiveOrZero BigDecimal price,
            @NotNull DifficultyLevel difficultyLevel,
            @NotNull CourseStatus status,
            @NotNull UUID categoryId,
            @NotNull UUID instructorId
    ) {
    }

    public record CourseResponse(UUID id, String title, String description, BigDecimal price,
                                 DifficultyLevel difficultyLevel, CourseStatus status,
                                 UUID categoryId, String categoryName, UUID instructorId,
                                 String instructorName, LocalDateTime publishedAt) {
    }

    public record ModuleRequest(@NotBlank String title, String description, @NotNull @Min(1) Integer position) {
    }

    public record ModuleResponse(UUID id, UUID courseId, String title, String description, Integer position) {
    }

    public record LessonRequest(@NotBlank String title, String contentUrl, @NotNull @Positive Integer durationMinutes,
                                @NotNull @Min(1) Integer position, boolean freePreview) {
    }

    public record LessonResponse(UUID id, UUID moduleId, String title, String contentUrl, Integer durationMinutes,
                                 Integer position, boolean freePreview) {
    }

    public record EnrollmentResponse(UUID id, UUID studentId, UUID courseId, BigDecimal progressPercent,
                                     EnrollmentStatus status, LocalDateTime enrolledAt) {
    }

    public record PaymentRequest(@NotNull UUID studentId, @NotNull UUID courseId,
                                 @NotNull @PositiveOrZero BigDecimal amount, @NotNull PaymentStatus status) {
    }

    public record PaymentResponse(UUID id, UUID studentId, UUID courseId, BigDecimal amount, PaymentStatus status,
                                  LocalDateTime paidAt, LocalDateTime createdAt) {
    }

    public record AssignmentRequest(@NotBlank String title, String description,
                                    @NotNull @Positive BigDecimal maxScore, @Min(1) Integer deadlineDays) {
    }

    public record AssignmentResponse(UUID id, UUID lessonId, String title, String description,
                                     BigDecimal maxScore, Integer deadlineDays) {
    }

    public record SubmissionRequest(@NotBlank String answerText) {
    }

    public record SubmissionResponse(UUID id, UUID assignmentId, UUID studentId, String answerText,
                                     SubmissionStatus status, LocalDateTime submittedAt) {
    }

    public record GradeRequest(@NotNull @PositiveOrZero BigDecimal score, String feedback) {
    }

    public record GradeResponse(UUID id, UUID submissionId, UUID graderId, BigDecimal score,
                                String feedback, LocalDateTime gradedAt) {
    }

    public record CertificateResponse(UUID id, UUID studentId, UUID courseId, String certificateNumber,
                                      LocalDateTime issuedAt) {
    }

    public record ReviewRequest(@NotNull UUID userId, @NotNull @Min(1) @Max(5) Integer rating,
                                @NotBlank String text) {
    }

    public record ReviewResponse(String id, UUID courseId, UUID userId, Integer rating, String text,
                                 LocalDateTime createdAt) {
    }

    public record CommentRequest(@NotNull UUID userId, @NotBlank String text) {
    }

    public record ReplyRequest(@NotNull UUID userId, @NotBlank String text) {
    }

    public record CommentReplyDto(UUID userId, String text, LocalDateTime createdAt) {
    }

    public record CommentResponse(String id, UUID lessonId, UUID userId, String text,
                                  List<CommentReplyDto> replies, LocalDateTime createdAt) {
    }

    public record ActivityLogRequest(@NotNull UUID userId, @NotBlank String action, UUID courseId,
                                     UUID lessonId, Map<String, Object> metadata) {
    }

    public record ActivityLogResponse(String id, UUID userId, String action, UUID courseId, UUID lessonId,
                                      LocalDateTime timestamp, Map<String, Object> metadata) {
    }
}
