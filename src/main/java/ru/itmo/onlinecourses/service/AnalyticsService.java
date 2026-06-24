package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> topCourses() {
        return jdbcTemplate.queryForList("""
                SELECT c.id, c.title, COUNT(e.id) AS students_count
                FROM courses c
                LEFT JOIN enrollments e ON e.course_id = c.id
                GROUP BY c.id, c.title
                ORDER BY students_count DESC, c.title
                """);
    }

    public List<Map<String, Object>> courseAverageProgress() {
        return jdbcTemplate.queryForList("""
                SELECT c.id, c.title, ROUND(COALESCE(AVG(e.progress_percent), 0), 2) AS average_progress
                FROM courses c
                LEFT JOIN enrollments e ON e.course_id = c.id
                GROUP BY c.id, c.title
                ORDER BY c.title
                """);
    }

    public List<Map<String, Object>> studentAverageGrades() {
        return jdbcTemplate.queryForList("""
                SELECT u.id AS student_id,
                       u.first_name || ' ' || u.last_name AS student_name,
                       c.id AS course_id,
                       c.title AS course_title,
                       ROUND(AVG(g.score), 2) AS average_grade
                FROM grades g
                JOIN submissions s ON s.id = g.submission_id
                JOIN assignments a ON a.id = s.assignment_id
                JOIN lessons l ON l.id = a.lesson_id
                JOIN course_modules cm ON cm.id = l.module_id
                JOIN courses c ON c.id = cm.course_id
                JOIN users u ON u.id = s.student_id
                GROUP BY u.id, u.first_name, u.last_name, c.id, c.title
                ORDER BY average_grade DESC
                """);
    }

    public List<Map<String, Object>> instructorIncome() {
        return jdbcTemplate.queryForList("""
                SELECT i.id AS instructor_id,
                       i.first_name || ' ' || i.last_name AS instructor_name,
                       SUM(p.amount) AS income
                FROM payments p
                JOIN courses c ON c.id = p.course_id
                JOIN users i ON i.id = c.instructor_id
                WHERE p.status = 'PAID'
                GROUP BY i.id, i.first_name, i.last_name
                ORDER BY income DESC
                """);
    }

    public List<Map<String, Object>> studentRanking() {
        return jdbcTemplate.queryForList("""
                WITH totals AS (
                    SELECT u.id AS student_id,
                           u.first_name || ' ' || u.last_name AS student_name,
                           COALESCE(SUM(g.score), 0) AS total_score
                    FROM users u
                    JOIN user_roles ur ON ur.user_id = u.id
                    JOIN roles r ON r.id = ur.role_id AND r.name = 'STUDENT'
                    LEFT JOIN submissions s ON s.student_id = u.id
                    LEFT JOIN grades g ON g.submission_id = s.id
                    GROUP BY u.id, u.first_name, u.last_name
                )
                SELECT student_id, student_name, total_score,
                       DENSE_RANK() OVER (ORDER BY total_score DESC) AS rank
                FROM totals
                ORDER BY rank, student_name
                """);
    }

    public List<Map<String, Object>> coursesWithProgressAbove(BigDecimal value) {
        return jdbcTemplate.queryForList("""
                SELECT c.id, c.title, ROUND(AVG(e.progress_percent), 2) AS average_progress
                FROM courses c
                JOIN enrollments e ON e.course_id = c.id
                GROUP BY c.id, c.title
                HAVING AVG(e.progress_percent) > ?
                ORDER BY average_progress DESC
                """, value);
    }

    public List<Map<String, Object>> inactiveStudents() {
        return jdbcTemplate.queryForList("""
                SELECT u.id AS student_id,
                       u.first_name || ' ' || u.last_name AS student_name,
                       c.id AS course_id,
                       c.title AS course_title
                FROM enrollments e
                JOIN users u ON u.id = e.student_id
                JOIN courses c ON c.id = e.course_id
                LEFT JOIN lesson_progress lp ON lp.enrollment_id = e.id
                WHERE lp.id IS NULL
                ORDER BY student_name, course_title
                """);
    }

    public List<Map<String, Object>> categoryStatistics() {
        return jdbcTemplate.queryForList("""
                SELECT cat.id,
                       cat.name,
                       COUNT(DISTINCT c.id) AS courses_count,
                       ROUND(COALESCE(AVG(c.price), 0), 2) AS average_price,
                       COUNT(DISTINCT e.student_id) AS students_count
                FROM categories cat
                LEFT JOIN courses c ON c.category_id = cat.id
                LEFT JOIN enrollments e ON e.course_id = c.id
                GROUP BY cat.id, cat.name
                ORDER BY cat.name
                """);
    }

    public List<Map<String, Object>> coursesWithoutPayments() {
        return jdbcTemplate.queryForList("""
                SELECT c.id, c.title, COUNT(e.id) AS enrollments_count
                FROM courses c
                JOIN enrollments e ON e.course_id = c.id
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM payments p
                    WHERE p.course_id = c.id
                      AND p.student_id = e.student_id
                      AND p.status = 'PAID'
                )
                GROUP BY c.id, c.title
                ORDER BY enrollments_count DESC
                """);
    }

    public List<Map<String, Object>> monthlyIncome() {
        return jdbcTemplate.queryForList("""
                SELECT DATE_TRUNC('month', paid_at) AS month,
                       SUM(amount) AS income
                FROM payments
                WHERE status = 'PAID' AND paid_at IS NOT NULL
                GROUP BY DATE_TRUNC('month', paid_at)
                ORDER BY month
                """);
    }
}
