package com.gamerspawn.backend.repository;

import com.gamerspawn.backend.entity.Event;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findAllByOrderByEventDateAsc();

    List<Event> findByTitleContainingIgnoreCaseAndVideoGameContainingIgnoreCaseAndEventTypeContainingIgnoreCaseAndCategoryContainingIgnoreCaseAndCountryContainingIgnoreCaseAndCityContainingIgnoreCaseAndModalityContainingIgnoreCaseAndEventDateGreaterThanEqualOrderByEventDateAsc(
            String title,
            String videoGame,
            String eventType,
            String category,
            String country,
            String city,
            String modality,
            LocalDate eventDate);
}
