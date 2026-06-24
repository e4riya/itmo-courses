package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.onlinecourses.dto.ApiDtos.ActivityLogRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.ActivityLogResponse;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.mongo.document.UserActivityLog;
import ru.itmo.onlinecourses.mongo.repository.UserActivityLogRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityLogService {
    private final UserActivityLogRepository activityLogRepository;
    private final EntityMapper mapper;

    public ActivityLogResponse create(ActivityLogRequest request) {
        UserActivityLog log = new UserActivityLog();
        log.setUserId(request.userId());
        log.setAction(request.action());
        log.setCourseId(request.courseId());
        log.setLessonId(request.lessonId());
        log.setMetadata(request.metadata() == null ? new HashMap<>() : request.metadata());
        log.setTimestamp(LocalDateTime.now());
        return mapper.toActivityLogResponse(activityLogRepository.save(log));
    }

    public List<ActivityLogResponse> findByUser(UUID userId) {
        return activityLogRepository.findByUserIdOrderByTimestampDesc(userId).stream()
                .map(mapper::toActivityLogResponse)
                .toList();
    }
}
