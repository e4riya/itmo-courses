package ru.itmo.onlinecourses.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.itmo.onlinecourses.mongo.document.CourseReview;

import java.util.List;
import java.util.UUID;

public interface CourseReviewRepository extends MongoRepository<CourseReview, String> {
    List<CourseReview> findByCourseId(UUID courseId);
}
