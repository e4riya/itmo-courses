package ru.itmo.onlinecourses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.onlinecourses.entity.Lesson;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findByModuleIdOrderByPosition(UUID moduleId);
}
