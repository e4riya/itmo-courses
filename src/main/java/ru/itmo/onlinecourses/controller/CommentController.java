package ru.itmo.onlinecourses.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.CommentRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.CommentResponse;
import ru.itmo.onlinecourses.dto.ApiDtos.ReplyRequest;
import ru.itmo.onlinecourses.service.CommentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/api/lessons/{lessonId}/comments")
    public CommentResponse create(@PathVariable UUID lessonId, @Valid @RequestBody CommentRequest request) {
        return commentService.create(lessonId, request);
    }

    @PostMapping("/api/comments/{commentId}/replies")
    public CommentResponse reply(@PathVariable String commentId, @Valid @RequestBody ReplyRequest request) {
        return commentService.reply(commentId, request);
    }

    @GetMapping("/api/lessons/{lessonId}/comments")
    public List<CommentResponse> byLesson(@PathVariable UUID lessonId) {
        return commentService.findByLesson(lessonId);
    }
}
