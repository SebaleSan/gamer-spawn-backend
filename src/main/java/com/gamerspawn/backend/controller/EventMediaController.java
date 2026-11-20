package com.gamerspawn.backend.controller;

import com.gamerspawn.backend.entity.Event;
import com.gamerspawn.backend.entity.EventMedia;
import com.gamerspawn.backend.entity.EventMedia.MediaType;
import com.gamerspawn.backend.entity.User;
import com.gamerspawn.backend.repository.EventMediaRepository;
import com.gamerspawn.backend.repository.EventRepository;
import com.gamerspawn.backend.repository.UserRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/private/events/{eventId}/media")
public class EventMediaController {

    private static final int MAX_URL_LENGTH = 500;
    private static final Pattern YOUTUBE_ID = Pattern.compile("[A-Za-z0-9_-]{11}");
    private static final Set<String> YOUTUBE_HOSTS = Set.of(
            "youtube.com",
            "www.youtube.com",
            "m.youtube.com");
    private static final String YOUTUBE_EMBED_URL = "https://www.youtube-nocookie.com/embed/";

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMediaRepository mediaRepository;

    public EventMediaController(
            EventRepository eventRepository,
            UserRepository userRepository,
            EventMediaRepository mediaRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
    }

    public record MediaRequest(MediaType type, String url) {
    }

    public record MediaResponse(
            Long id,
            MediaType type,
            String url,
            String username,
            LocalDateTime createdAt) {
    }

    @GetMapping
    public List<MediaResponse> getMedia(@PathVariable Long eventId) {
        findEvent(eventId);
        return mediaRepository.findByEventIdOrderByCreatedAtAsc(eventId)
                .stream()
                .map(this::toMediaResponse)
                .toList();
    }

    @PostMapping
    public MediaResponse addMedia(
            @PathVariable Long eventId,
            @RequestBody MediaRequest request,
            Authentication authentication) {
        if (request == null || request.type() == null) {
            throw badRequest("El tipo debe ser PHOTO o VIDEO.");
        }
        String url = normalizeUrl(request.type(), request.url());
        Event event = findEvent(eventId);
        User user = findUser(authentication);

        EventMedia media = new EventMedia();
        media.setEvent(event);
        media.setUser(user);
        media.setType(request.type());
        media.setUrl(url);
        media.setCreatedAt(LocalDateTime.now());

        return toMediaResponse(mediaRepository.save(media));
    }

    @DeleteMapping("/{mediaId}")
    public void deleteMedia(
            @PathVariable Long eventId,
            @PathVariable Long mediaId,
            Authentication authentication) {
        findEvent(eventId);

        EventMedia media = mediaRepository.findByIdAndEventId(mediaId, eventId)
                .filter(found -> canDelete(found, authentication))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Contenido no encontrado."));

        mediaRepository.delete(media);
    }

    static String normalizeUrl(MediaType type, String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw badRequest("La URL no puede estar vacía.");
        }

        String url = rawUrl.trim();
        if (url.length() > MAX_URL_LENGTH) {
            throw badRequest("La URL no puede superar los " + MAX_URL_LENGTH + " caracteres.");
        }

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException exception) {
            throw badRequest("La URL tiene un formato inválido.");
        }

        if (!"https".equalsIgnoreCase(uri.getScheme())
                || uri.getHost() == null
                || uri.getRawUserInfo() != null) {
            throw badRequest("La URL debe comenzar con https://.");
        }

        if (type == MediaType.VIDEO) {
            return YOUTUBE_EMBED_URL + extractYoutubeId(uri);
        }
        return uri.toString();
    }

    private static String extractYoutubeId(URI uri) {
        String host = uri.getHost().toLowerCase(Locale.ROOT);
        String path = uri.getPath() == null ? "" : uri.getPath();
        String videoId = null;

        if (host.equals("youtu.be") && path.length() > 1) {
            videoId = path.substring(1);
        } else if (YOUTUBE_HOSTS.contains(host)) {
            if (path.equals("/watch")) {
                videoId = UriComponentsBuilder.fromUri(uri)
                        .build()
                        .getQueryParams()
                        .getFirst("v");
            } else if (path.startsWith("/shorts/") || path.startsWith("/embed/")) {
                videoId = path.substring(path.indexOf('/', 1) + 1);
            }
        }

        if (videoId == null || !YOUTUBE_ID.matcher(videoId).matches()) {
            throw badRequest("El video debe ser un enlace válido de YouTube.");
        }
        return videoId;
    }

    private boolean canDelete(EventMedia media, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        boolean isOwner = media.getUser() != null
                && media.getUser().getUsername().equals(authentication.getName());
        return isAdmin || isOwner;
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evento no encontrado."));
    }

    private User findUser(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no encontrado.");
        }
        return user;
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private MediaResponse toMediaResponse(EventMedia media) {
        return new MediaResponse(
                media.getId(),
                media.getType(),
                media.getUrl(),
                media.getUser() == null ? null : media.getUser().getUsername(),
                media.getCreatedAt());
    }
}
