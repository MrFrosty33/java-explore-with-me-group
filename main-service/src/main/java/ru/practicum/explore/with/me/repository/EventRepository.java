package ru.practicum.explore.with.me.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.with.me.model.event.Event;
import ru.practicum.explore.with.me.model.user.User;

import java.time.LocalDateTime;
import java.util.List;
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
              AND (:text IS NULL
                OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))
                OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')))
              AND (:categories IS NULL OR e.category.id IN :categories)
              AND (:paid IS NULL OR e.paid = :paid)
              AND (
                  (:rangeStart IS NULL AND :rangeEnd IS NULL AND e.eventDate > CURRENT_TIMESTAMP)
                  OR
                  (:rangeStart IS NOT NULL AND :rangeEnd IS NOT NULL AND e.eventDate BETWEEN :rangeStart AND :rangeEnd)
                  OR
                  (:rangeStart IS NOT NULL AND :rangeEnd IS NULL AND e.eventDate >= :rangeStart)
                  OR
                  (:rangeStart IS NULL AND :rangeEnd IS NOT NULL AND e.eventDate <= :rangeEnd)
              )
            """)
    Page<Event> findPublicEvents(@Param("text") String text,
                                 @Param("categories") List<Long> categories,
                                 @Param("paid") Boolean paid,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 Pageable pageable);


}
