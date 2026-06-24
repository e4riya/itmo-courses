package ru.itmo.onlinecourses.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.itmo.onlinecourses.mongo.document.UserActivityLog;

import java.util.List;
import java.util.UUID;

public interface UserActivityLogRepository extends MongoRepository<UserActivityLog, String> {
    List<UserActivityLog> findByUserIdOrderByTimestampDesc(UUID userId);
}
