package ru.itmo.onlinecourses.mapper;

import org.springframework.stereotype.Component;
import ru.itmo.onlinecourses.dto.ApiDtos.*;
import ru.itmo.onlinecourses.entity.*;
import ru.itmo.onlinecourses.enums.RoleName;
import ru.itmo.onlinecourses.mongo.document.CourseReview;
import ru.itmo.onlinecourses.mongo.document.LessonComment;
import ru.itmo.onlinecourses.mongo.document.UserActivityLog;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EntityMapper {
    public UserResponse toUserResponse(User user) {
        Set<RoleName> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.isActive(), roles, user.getCreatedAt());
    }

    public CourseResponse toCourseResponse(Course course) {
        return new CourseResponse(course.getId(), course.getTitle(), course.getDescription(), course.getPrice(),
                course.getDifficultyLevel(), course.getStatus(), course.getCategory().getId(),
                course.getCategory().getName(), course.getInstructor().getId(),
                course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName(),
                course.getPublishedAt());
    }

    public ModuleResponse toModuleResponse(CourseModule module) {
        return new ModuleResponse(module.getId(), module.getCourse().getId(), module.getTitle(),
                module.getDescription(), module.getPosition());
    }

    public LessonResponse toLessonResponse(Lesson lesson) {
        return new LessonResponse(lesson.getId(), lesson.getModule().getId(), lesson.getTitle(),
                lesson.getContentUrl(), lesson.getDurationMinutes(), lesson.getPosition(), lesson.isFreePreview());
    }

    public EnrollmentResponse toEnrollmentResponse(Enrollment enrollment) {
        return new EnrollmentResponse(enrollment.getId(), enrollment.getStudent().getId(), enrollment.getCourse().getId(),
                enrollment.getProgressPercent(), enrollment.getStatus(), enrollment.getEnrolledAt());
    }

    public PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getStudent().getId(), payment.getCourse().getId(),
                payment.getAmount(), payment.getStatus(), payment.getPaidAt(), payment.getCreatedAt());
    }

    public AssignmentResponse toAssignmentResponse(Assignment assignment) {
        return new AssignmentResponse(assignment.getId(), assignment.getLesson().getId(), assignment.getTitle(),
                assignment.getDescription(), assignment.getMaxScore(), assignment.getDeadlineDays());
    }

    public SubmissionResponse toSubmissionResponse(Submission submission) {
        return new SubmissionResponse(submission.getId(), submission.getAssignment().getId(), submission.getStudent().getId(),
                submission.getAnswerText(), submission.getStatus(), submission.getSubmittedAt());
    }

    public GradeResponse toGradeResponse(Grade grade) {
        return new GradeResponse(grade.getId(), grade.getSubmission().getId(), grade.getGrader().getId(),
                grade.getScore(), grade.getFeedback(), grade.getGradedAt());
    }

    public CertificateResponse toCertificateResponse(Certificate certificate) {
        return new CertificateResponse(certificate.getId(), certificate.getStudent().getId(), certificate.getCourse().getId(),
                certificate.getCertificateNumber(), certificate.getIssuedAt());
    }

    public ReviewResponse toReviewResponse(CourseReview review) {
        return new ReviewResponse(review.getId(), review.getCourseId(), review.getUserId(),
                review.getRating(), review.getText(), review.getCreatedAt());
    }

    public CommentResponse toCommentResponse(LessonComment comment) {
        return new CommentResponse(comment.getId(), comment.getLessonId(), comment.getUserId(), comment.getText(),
                comment.getReplies().stream()
                        .map(reply -> new CommentReplyDto(reply.getUserId(), reply.getText(), reply.getCreatedAt()))
                        .toList(),
                comment.getCreatedAt());
    }

    public ActivityLogResponse toActivityLogResponse(UserActivityLog log) {
        return new ActivityLogResponse(log.getId(), log.getUserId(), log.getAction(), log.getCourseId(),
                log.getLessonId(), log.getTimestamp(), log.getMetadata());
    }
}
