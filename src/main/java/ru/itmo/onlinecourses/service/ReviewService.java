package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.onlinecourses.dto.ApiDtos.ReviewRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.ReviewResponse;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.mongo.document.CourseReview;
import ru.itmo.onlinecourses.mongo.repository.CourseReviewRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final CourseReviewRepository reviewRepository;
    private final EntityMapper mapper;

    public ReviewResponse create(UUID courseId, ReviewRequest request) {
        CourseReview review = new CourseReview();
        review.setCourseId(courseId);
        review.setUserId(request.userId());
        review.setRating(request.rating());
        review.setText(request.text());
        review.setCreatedAt(LocalDateTime.now());
        return mapper.toReviewResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> findByCourse(UUID courseId) {
        return reviewRepository.findByCourseId(courseId).stream().map(mapper::toReviewResponse).toList();
    }

    public double averageRating(UUID courseId) {
        return reviewRepository.findByCourseId(courseId).stream()
                .mapToInt(CourseReview::getRating)
                .average()
                .orElse(0);
    }
}
