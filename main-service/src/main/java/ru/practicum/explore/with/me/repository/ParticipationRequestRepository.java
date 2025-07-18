package ru.practicum.explore.with.me.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.with.me.model.participation.ParticipationRequest;
import ru.practicum.explore.with.me.model.participation.ParticipationRequestStatus;

import java.util.List;
import java.util.Map;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);


    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    int countByEventId(Long eventId);

    @Query("""
                SELECT r.event.id, COUNT(r)
                    FROM ParticipationRequest r
                    WHERE r.event.id IN :eventIds
                    GROUP BY r.event.id
            """)
    Map<Long, Integer> countGroupByEventId(@Param("eventIds") List<Long> eventIds);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    @Modifying
    @Query("""
                UPDATE ParticipationRequest pr
                SET pr.status = :status
                WHERE pr.id IN :requestIds
            """)
    void updateStatus(@Param("requestIds") List<Long> requestIds, @Param("status") ParticipationRequestStatus status);

}
