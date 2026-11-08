package com.gamerspawn.backend.controller;

import com.gamerspawn.backend.entity.Event;
import com.gamerspawn.backend.repository.EventRepository;
import java.util.List;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/events")
    public List<Event> getEvents() {
        return eventRepository.findAllByOrderByEventDateAsc();
    }

    @GetMapping("/events/search")
    public List<Event> searchEvents(
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "") String videoGame,
            @RequestParam(defaultValue = "") String eventType,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String country,
            @RequestParam(defaultValue = "") String city,
            @RequestParam(defaultValue = "") String modality,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate) {
        return eventRepository
                .findByTitleContainingIgnoreCaseAndVideoGameContainingIgnoreCaseAndEventTypeContainingIgnoreCaseAndCategoryContainingIgnoreCaseAndCountryContainingIgnoreCaseAndCityContainingIgnoreCaseAndModalityContainingIgnoreCaseAndEventDateGreaterThanEqualOrderByEventDateAsc(
                        title,
                        videoGame,
                        eventType,
                        category,
                        country,
                        city,
                        modality,
                        eventDate == null ? LocalDate.now() : eventDate);
    }

    @GetMapping("/private/events")
    public List<Event> getPrivateEvents() {
        return getEvents();
    }

    @GetMapping("/private/events/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
