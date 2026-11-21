package com.gamerspawn.backend.controller;

import com.gamerspawn.backend.entity.EventRating;
import com.gamerspawn.backend.entity.User;
import com.gamerspawn.backend.repository.EventRatingRepository;
import com.gamerspawn.backend.repository.EventRepository;
import com.gamerspawn.backend.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/private/events/{eventId}/rating")
public class EventRatingController {

    private final EventRepository eventRepository;
    private final EventRatingRepository ratingRepository;
    private final UserRepository userRepository;

    public EventRatingController(
            EventRepository eventRepository,
            EventRatingRepository ratingRepository,
            UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    public record RatingResponse(
            Double averageScore,
            Long totalRatings,
            Integer myScore) {
    }

    public record RatingRequest(Integer score) {
    }

    @GetMapping
    public RatingResponse getRating(
            @PathVariable Long eventId,
            Authentication authentication) {
        findEvent(eventId);

        Integer myScore = ratingRepository
                .findByEventIdAndUserUsername(
                        eventId,
                        authentication.getName())
                .map(EventRating::getScore)
                .orElse(null);

        return new RatingResponse(
                ratingRepository.findAverageScoreByEventId(eventId),
                ratingRepository.countByEventId(eventId),
                myScore);
    }

    @PutMapping
    public RatingResponse saveRating(
            @PathVariable Long eventId,
            @RequestBody RatingRequest request,
            Authentication authentication) {
        if (request == null
                || request.score() == null
                || request.score() < 1
                || request.score() > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La valoración debe estar entre 1 y 5.");
        }

        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evento no encontrado."));

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no encontrado.");
        }

        LocalDateTime now = LocalDateTime.now();
        EventRating rating = ratingRepository
                .findByEventIdAndUserUsername(eventId, user.getUsername())
                .orElseGet(EventRating::new);

        if (rating.getId() == null) {
            rating.setEvent(event);
            rating.setUser(user);
            rating.setCreatedAt(now);
        }
        rating.setScore(request.score());
        rating.setUpdatedAt(now);
        ratingRepository.save(rating);

        Integer myScore = request.score();
        return new RatingResponse(
                ratingRepository.findAverageScoreByEventId(eventId),
                ratingRepository.countByEventId(eventId),
                myScore);
    }

    @DeleteMapping
    public RatingResponse deleteRating(
            @PathVariable Long eventId,
            Authentication authentication) {
        findEvent(eventId);

        EventRating rating = ratingRepository
                .findByEventIdAndUserUsername(
                        eventId,
                        authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una valoración del usuario."));

        ratingRepository.delete(rating);

        return new RatingResponse(
                ratingRepository.findAverageScoreByEventId(eventId),
                ratingRepository.countByEventId(eventId),
                null);
    }

    private void findEvent(Long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evento no encontrado."));
    }
}
