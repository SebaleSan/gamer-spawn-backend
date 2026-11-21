package com.gamerspawn.backend.repository;

import com.gamerspawn.backend.entity.EventMedia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventMediaRepository
        extends JpaRepository<EventMedia, Long> {

    boolean existsByEventIdAndUrl(Long eventId, String url);

    List<EventMedia> findByEventIdOrderByCreatedAtAsc(Long eventId);

    Optional<EventMedia> findByIdAndEventId(Long mediaId, Long eventId);
}
