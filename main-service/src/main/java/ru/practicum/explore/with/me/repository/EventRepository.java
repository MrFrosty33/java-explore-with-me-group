package ru.practicum.explore.with.me.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.with.me.model.User;
import ru.practicum.explore.with.me.model.event.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("""
            SELECT DISTINCT e FROM Event e
            WHERE e.initiator = :user
            """)
    Page<Event> findEventsByUser(@Param("user") User user,
                                 Pageable pageable);
}
