package ru.itmo.onlinecourses.mongo.document;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "course_reviews")
public class CourseReview {
    @Id
    private String id;
    private UUID courseId;
    private UUID userId;
    @Min(1)
    @Max(5)
    private Integer rating;
    @NotBlank
    private String text;
    private LocalDateTime createdAt;
}
