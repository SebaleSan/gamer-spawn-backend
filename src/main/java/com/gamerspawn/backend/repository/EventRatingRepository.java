package com.gamerspawn.backend.repository;

import com.gamerspawn.backend.entity.EventRating;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRatingRepository
        extends JpaRepository<EventRating, Long> {

    Optional<EventRating> findByEventIdAndUserUsername(
            Long eventId,
            String username);

    @Query("select avg(r.score) from EventRating r where r.event.id = :eventId")
    Double findAverageScoreByEventId(@Param("eventId") Long eventId);

    long countByEventId(Long eventId);
}
