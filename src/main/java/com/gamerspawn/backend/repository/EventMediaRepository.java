package com.gamerspawn.backend.repository;

import com.gamerspawn.backend.entity.EventMedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventMediaRepository
        extends JpaRepository<EventMedia, Long> {

    boolean existsByEventIdAndUrl(Long eventId, String url);
}
