package ru.practicum.explore.with.me.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.with.me.model.user.User;
import ru.practicum.explore.with.me.model.event.Event;
import ru.practicum.explore.with.me.model.event.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("""
            SELECT DISTINCT e FROM Event e
            WHERE e.initiator = :user
            """)
    Page<Event> findEventsByUser(@Param("user") User user,
                                 Pageable pageable);

    @Query("""
            SELECT e
            FROM Event e
            WHERE (:users      IS NULL OR e.initiator.id IN :users)
              AND (:states     IS NULL OR e.state IN :states)
              AND (:categories IS NULL OR e.category.id IN :categories)
              AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
              AND (:rangeEnd   IS NULL OR e.eventDate <= :rangeEnd)
           """)
    Page<Event> searchForAdmin(@Param("users") List<Long> users,
                       @Param("states")     List<EventState> states,
                       @Param("categories") List<Long> categories,
                       @Param("rangeStart") LocalDateTime rangeStart,
                       @Param("rangeEnd")   LocalDateTime rangeEnd,
                       Pageable pageable);
}
