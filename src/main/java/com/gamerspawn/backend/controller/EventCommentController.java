package com.gamerspawn.backend.controller;

import com.gamerspawn.backend.entity.Event;
import com.gamerspawn.backend.entity.EventComment;
import com.gamerspawn.backend.entity.User;
import com.gamerspawn.backend.repository.EventCommentRepository;
import com.gamerspawn.backend.repository.EventRepository;
import com.gamerspawn.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/private/events/{eventId}/comments")
public class EventCommentController {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventCommentRepository commentRepository;

    public EventCommentController(
            EventRepository eventRepository,
            UserRepository userRepository,
            EventCommentRepository commentRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public record CommentRequest(String content) {
    }

    public record CommentResponse(
            Long id,
            String username,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
    }

    @GetMapping
    public List<CommentResponse> getComments(@PathVariable Long eventId) {
        findEvent(eventId);
        return commentRepository.findByEventIdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    @PostMapping
    public CommentResponse createComment(
            @PathVariable Long eventId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        String content = validateContent(request);
        Event event = findEvent(eventId);
        User user = findUser(authentication);
        LocalDateTime now = LocalDateTime.now();

        EventComment comment = new EventComment();
        comment.setEvent(event);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);

        return toCommentResponse(commentRepository.save(comment));
    }

    @PutMapping("/{commentId}")
    public CommentResponse updateComment(
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        findEvent(eventId);
        String content = validateContent(request);

        EventComment comment = commentRepository
                .findByIdAndUserUsername(commentId, authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Comentario no encontrado."));

        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Comentario no encontrado.");
        }

        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        return toCommentResponse(commentRepository.save(comment));
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            Authentication authentication) {
        findEvent(eventId);

        EventComment comment = commentRepository
                .findByIdAndUserUsername(commentId, authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Comentario no encontrado."));

        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Comentario no encontrado.");
        }

        commentRepository.delete(comment);
    }

    private String validateContent(CommentRequest request) {
        if (request == null || request.content() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El comentario no puede estar vacío.");
        }

        String content = request.content().trim();
        if (content.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El comentario no puede estar vacío.");
        }
        if (content.length() > 1000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El comentario no puede superar los 1000 caracteres.");
        }
        return content;
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evento no encontrado."));
    }

    private User findUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado.");
        }

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no encontrado.");
        }
        return user;
    }

    private CommentResponse toCommentResponse(EventComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getUser().getUsername(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }
}
