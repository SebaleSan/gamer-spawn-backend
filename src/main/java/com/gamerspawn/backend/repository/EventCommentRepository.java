package com.gamerspawn.backend.repository;

import com.gamerspawn.backend.entity.EventComment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCommentRepository
        extends JpaRepository<EventComment, Long> {

    List<EventComment> findByEventIdOrderByCreatedAtDesc(Long eventId);

    Optional<EventComment> findByIdAndUserUsername(
            Long commentId,
            String username);
}
