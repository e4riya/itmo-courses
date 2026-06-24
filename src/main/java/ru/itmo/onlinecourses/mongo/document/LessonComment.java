package ru.itmo.onlinecourses.mongo.document;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "lesson_comments")
public class LessonComment {
    @Id
    private String id;
    private UUID lessonId;
    private UUID userId;
    @NotBlank
    private String text;
    private List<CommentReply> replies = new ArrayList<>();
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommentReply {
        private UUID userId;
        private String text;
        private LocalDateTime createdAt;
    }
}
