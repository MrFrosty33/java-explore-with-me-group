package ru.practicum.explore.with.me.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.with.me.model.participation.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);


    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    int countByEventId(Long eventId);
}
