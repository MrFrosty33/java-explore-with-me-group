package ru.practicum.explore.with.me.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.with.me.model.event.Event;
import ru.practicum.explore.with.me.model.user.User;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("""
            SELECT DISTINCT e FROM Event e
            WHERE e.initiator = :user
            ORDER BY e.createdOn
            """)
    Page<Event> findEventsByUserWitOffsetAndLimit(@Param("user") User user,
                                                  Pageable pageable);
}
