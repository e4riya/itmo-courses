package ru.itmo.onlinecourses.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itmo.onlinecourses.dto.ApiDtos.*;
import ru.itmo.onlinecourses.enums.PaymentStatus;
import ru.itmo.onlinecourses.service.*;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebController {
    private static final UUID DEMO_STUDENT_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");

    private final CourseService courseService;
    private final ModuleService moduleService;
    private final LessonService lessonService;
    private final EnrollmentService enrollmentService;
    private final PaymentService paymentService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final GradeService gradeService;
    private final CertificateService certificateService;
    private final ReviewService reviewService;
    private final AnalyticsService analyticsService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("topCourses", analyticsService.topCourses());
        model.addAttribute("demoStudentId", DEMO_STUDENT_ID);
        return "home";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("demoStudentId", DEMO_STUDENT_ID);
        return "courses";
    }

    @GetMapping("/courses/{courseId}")
    public String course(@PathVariable UUID courseId,
                         @RequestParam(defaultValue = "10000000-0000-0000-0000-000000000001") UUID studentId,
                         Model model) {
        var modules = moduleService.findByCourse(courseId);
        model.addAttribute("course", courseService.findById(courseId));
        model.addAttribute("modules", modules);
        model.addAttribute("studentId", studentId);
        model.addAttribute("reviews", reviewService.findByCourse(courseId));
        model.addAttribute("averageRating", reviewService.averageRating(courseId));
        model.addAttribute("moduleService", moduleService);
        model.addAttribute("lessonService", lessonService);
        model.addAttribute("assignmentService", assignmentService);
        return "course-details";
    }

    @PostMapping("/courses/{courseId}/enroll")
    public String enroll(@PathVariable UUID courseId, @RequestParam UUID studentId, RedirectAttributes redirectAttributes) {
        enrollmentService.enroll(courseId, studentId);
        redirectAttributes.addFlashAttribute("message", "Студент записан на курс");
        return "redirect:/courses/" + courseId + "?studentId=" + studentId;
    }

    @PostMapping("/courses/{courseId}/pay")
    public String pay(@PathVariable UUID courseId,
                      @RequestParam UUID studentId,
                      @RequestParam BigDecimal amount,
                      RedirectAttributes redirectAttributes) {
        paymentService.create(new PaymentRequest(studentId, courseId, amount, PaymentStatus.PAID));
        redirectAttributes.addFlashAttribute("message", "Оплата создана. Enrollment добавляется триггером PostgreSQL");
        return "redirect:/courses/" + courseId + "?studentId=" + studentId;
    }

    @PostMapping("/courses/{courseId}/reviews")
    public String review(@PathVariable UUID courseId,
                         @RequestParam UUID userId,
                         @RequestParam Integer rating,
                         @RequestParam String text,
                         RedirectAttributes redirectAttributes) {
        reviewService.create(courseId, new ReviewRequest(userId, rating, text));
        redirectAttributes.addFlashAttribute("message", "Отзыв сохранен в MongoDB");
        return "redirect:/courses/" + courseId + "?studentId=" + userId;
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public String completeLesson(@PathVariable UUID lessonId,
                                 @RequestParam UUID courseId,
                                 @RequestParam UUID studentId,
                                 RedirectAttributes redirectAttributes) {
        lessonService.complete(lessonId, studentId);
        redirectAttributes.addFlashAttribute("message", "Урок завершен. Прогресс пересчитан триггером PostgreSQL");
        return "redirect:/courses/" + courseId + "?studentId=" + studentId;
    }

    @PostMapping("/assignments/{assignmentId}/submit")
    public String submitAssignment(@PathVariable UUID assignmentId,
                                   @RequestParam UUID courseId,
                                   @RequestParam UUID studentId,
                                   @RequestParam String answerText,
                                   RedirectAttributes redirectAttributes) {
        submissionService.submit(assignmentId, studentId, new SubmissionRequest(answerText));
        redirectAttributes.addFlashAttribute("message", "Решение отправлено");
        return "redirect:/courses/" + courseId + "?studentId=" + studentId;
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public String gradeSubmission(@PathVariable UUID submissionId,
                                  @RequestParam UUID studentId,
                                  @RequestParam UUID graderId,
                                  @RequestParam BigDecimal score,
                                  @RequestParam String feedback,
                                  RedirectAttributes redirectAttributes) {
        gradeService.grade(submissionId, graderId, new GradeRequest(score, feedback));
        redirectAttributes.addFlashAttribute("message", "Оценка сохранена. Диапазон проверяет PostgreSQL trigger");
        return "redirect:/students/" + studentId;
    }

    @GetMapping("/students/{studentId}")
    public String student(@PathVariable UUID studentId, Model model) {
        model.addAttribute("studentId", studentId);
        model.addAttribute("enrollments", enrollmentService.findByStudent(studentId));
        model.addAttribute("payments", paymentService.findByStudent(studentId));
        model.addAttribute("submissions", submissionService.findByStudent(studentId));
        model.addAttribute("certificates", certificateService.findByStudent(studentId));
        return "student";
    }

    @GetMapping("/analytics")
    public String analytics(@RequestParam(defaultValue = "70") BigDecimal value, Model model) {
        model.addAttribute("topCourses", analyticsService.topCourses());
        model.addAttribute("courseAverageProgress", analyticsService.courseAverageProgress());
        model.addAttribute("studentAverageGrades", analyticsService.studentAverageGrades());
        model.addAttribute("instructorIncome", analyticsService.instructorIncome());
        model.addAttribute("studentRanking", analyticsService.studentRanking());
        model.addAttribute("coursesWithProgressAbove", analyticsService.coursesWithProgressAbove(value));
        model.addAttribute("inactiveStudents", analyticsService.inactiveStudents());
        model.addAttribute("categoryStatistics", analyticsService.categoryStatistics());
        model.addAttribute("coursesWithoutPayments", analyticsService.coursesWithoutPayments());
        model.addAttribute("monthlyIncome", analyticsService.monthlyIncome());
        model.addAttribute("value", value);
        return "analytics";
    }

    @ExceptionHandler(RuntimeException.class)
    public String webError(RuntimeException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "web-error";
    }
}
