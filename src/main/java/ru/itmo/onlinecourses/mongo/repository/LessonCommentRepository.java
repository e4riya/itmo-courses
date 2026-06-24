package ru.itmo.onlinecourses.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.itmo.onlinecourses.mongo.document.LessonComment;

import java.util.List;
import java.util.UUID;

public interface LessonCommentRepository extends MongoRepository<LessonComment, String> {
    List<LessonComment> findByLessonId(UUID lessonId);
}
