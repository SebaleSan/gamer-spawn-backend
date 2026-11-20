package com.gamerspawn.backend.controller;

import com.gamerspawn.backend.entity.Event;
import com.gamerspawn.backend.entity.User;
import com.gamerspawn.backend.repository.EventRepository;
import com.gamerspawn.backend.repository.UserRepository;
import com.gamerspawn.backend.validation.SafeUrls;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class EventController {

    private static final int MAX_TEXT_LENGTH = 255;
    private static final Set<String> MODALITIES = Set.of("PRESENCIAL", "ONLINE");

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventController(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public record EventRequest(
            String title,
            LocalDate eventDate,
            LocalTime eventTime,
            String location,
            String accessLink,
            String eventType,
            String videoGame,
            String category,
            String country,
            String city,
            String modality,
            String description,
            String organizer,
            String participationRequirements,
            String registrationInformation,
            String imageUrl) {
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

    @GetMapping("/private/events/mine")
    public List<Event> getMyEvents(Authentication authentication) {
        return eventRepository.findByCreatedByUsernameOrderByEventDateAsc(authentication.getName());
    }

    @GetMapping("/private/events/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/private/events")
    public Event createEvent(
            @RequestBody EventRequest request,
            Authentication authentication) {
        if (request == null) {
            throw SafeUrls.badRequest("Los datos del evento son obligatorios.");
        }

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no encontrado.");
        }

        if (request.eventDate() == null || request.eventDate().isBefore(LocalDate.now())) {
            throw SafeUrls.badRequest("La fecha del evento es obligatoria y no puede ser anterior a hoy.");
        }

        String modality = requiredText(request.modality(), "modalidad").toUpperCase(Locale.ROOT);
        if (!MODALITIES.contains(modality)) {
            throw SafeUrls.badRequest("La modalidad debe ser PRESENCIAL u ONLINE.");
        }

        String organizer = optionalText(request.organizer(), "organizador");

        Event event = new Event();
        event.setTitle(requiredText(request.title(), "título"));
        event.setEventDate(request.eventDate());
        event.setEventTime(request.eventTime());
        event.setLocation(requiredText(request.location(), "ubicación"));
        event.setAccessLink(optionalUrl(request.accessLink(), false));
        event.setEventType(requiredText(request.eventType(), "tipo de evento"));
        event.setVideoGame(requiredText(request.videoGame(), "videojuego"));
        event.setCategory(requiredText(request.category(), "categoría"));
        event.setCountry(requiredText(request.country(), "país"));
        event.setCity(requiredText(request.city(), "ciudad"));
        event.setModality(modality);
        event.setDescription(requiredText(request.description(), "descripción"));
        event.setOrganizer(organizer == null ? user.getUsername() : organizer);
        event.setParticipationRequirements(
                optionalText(request.participationRequirements(), "requisitos de participación"));
        event.setRegistrationInformation(
                optionalText(request.registrationInformation(), "información de inscripción"));
        event.setImageUrl(optionalUrl(request.imageUrl(), true));
        event.setCreatedBy(user);

        return eventRepository.save(event);
    }

    private static String requiredText(String value, String fieldName) {
        String text = optionalText(value, fieldName);
        if (text == null) {
            throw SafeUrls.badRequest("El campo " + fieldName + " es obligatorio.");
        }
        return text;
    }

    private static String optionalText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String text = value.trim();
        if (text.length() > MAX_TEXT_LENGTH) {
            throw SafeUrls.badRequest(
                    "El campo " + fieldName + " no puede superar los " + MAX_TEXT_LENGTH + " caracteres.");
        }
        return text;
    }

    private static String optionalUrl(String value, boolean usedInCss) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return usedInCss
                ? SafeUrls.requireCssSafeHttps(value, MAX_TEXT_LENGTH)
                : SafeUrls.requireHttps(value, MAX_TEXT_LENGTH).toString();
    }
}
