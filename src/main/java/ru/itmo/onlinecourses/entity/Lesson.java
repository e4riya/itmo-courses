package ru.itmo.onlinecourses.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lessons", uniqueConstraints = @UniqueConstraint(columnNames = {"module_id", "position"}))
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule module;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "content_url", length = 500)
    private String contentUrl;

    @Positive
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "is_free_preview", nullable = false)
    private boolean freePreview;

    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY)
    private List<Assignment> assignments = new ArrayList<>();
}
