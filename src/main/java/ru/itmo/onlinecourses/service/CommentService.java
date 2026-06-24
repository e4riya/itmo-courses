package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.onlinecourses.dto.ApiDtos.CommentRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.CommentResponse;
import ru.itmo.onlinecourses.dto.ApiDtos.ReplyRequest;
import ru.itmo.onlinecourses.exception.NotFoundException;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.mongo.document.LessonComment;
import ru.itmo.onlinecourses.mongo.repository.LessonCommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final LessonCommentRepository commentRepository;
    private final EntityMapper mapper;

    public CommentResponse create(UUID lessonId, CommentRequest request) {
        LessonComment comment = new LessonComment();
        comment.setLessonId(lessonId);
        comment.setUserId(request.userId());
        comment.setText(request.text());
        comment.setCreatedAt(LocalDateTime.now());
        return mapper.toCommentResponse(commentRepository.save(comment));
    }

    public CommentResponse reply(String commentId, ReplyRequest request) {
        LessonComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));
        LessonComment.CommentReply reply = new LessonComment.CommentReply();
        reply.setUserId(request.userId());
        reply.setText(request.text());
        reply.setCreatedAt(LocalDateTime.now());
        comment.getReplies().add(reply);
        return mapper.toCommentResponse(commentRepository.save(comment));
    }

    public List<CommentResponse> findByLesson(UUID lessonId) {
        return commentRepository.findByLessonId(lessonId).stream().map(mapper::toCommentResponse).toList();
    }
}
