package ru.practicum.explore.with.me.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.with.me.model.event.Event;
import ru.practicum.explore.with.me.model.user.User;
import ru.practicum.explore.with.me.model.event.PublicEventParams;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("""
            SELECT DISTINCT e FROM Event e
            WHERE e.initiator = :user
            """)
    Page<Event> findEventsByUser(@Param("user") User user,
                                 Pageable pageable);

    Optional<Event> findByIdAndState(Long id, String state);

    @Query("""
            SELECT e FROM Event AS e
            WHERE e.state = 'PUBLISHED'
            AND (:#{#params.text} IS NULL
            OR LOWER(e.description) LIKE LOWER(CONCAT('%', :#{#params.text}, '%'))
            OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :#{#params.text}, '%')))
            AND (:#{#params.categories} IS NULL OR e.category.id IN (:#{#params.categories}))
            AND (:#{#params.paid} IS NULL OR e.paid = :#{#params.paid})
            AND ((coalesce(:#{#params.rangeStart}, :#{#params.rangeEnd}) IS NULL AND e.eventDate > now())
            OR e.eventDate BETWEEN coalesce(:#{#params.rangeStart}, e.eventDate) AND coalesce(:#{#params.rangeEnd}, e.eventDate))
            """)
    Page<Event> findPublicEvents(@Param("params") PublicEventParams params, Pageable pageable);


}
