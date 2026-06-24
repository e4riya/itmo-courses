package ru.itmo.onlinecourses.mongo.document;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "user_activity_logs")
public class UserActivityLog {
    @Id
    private String id;
    private UUID userId;
    @NotBlank
    private String action;
    private UUID courseId;
    private UUID lessonId;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata = new HashMap<>();
}
