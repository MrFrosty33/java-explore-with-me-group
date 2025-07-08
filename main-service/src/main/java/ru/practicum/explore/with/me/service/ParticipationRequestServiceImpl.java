package ru.practicum.explore.with.me.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.with.me.exception.ConflictException;
import ru.practicum.explore.with.me.exception.NotFoundException;
import ru.practicum.explore.with.me.mapper.ParticipationRequestMapper;
import ru.practicum.explore.with.me.model.event.Event;
import ru.practicum.explore.with.me.model.participation.CancelParticipationRequest;
import ru.practicum.explore.with.me.model.participation.NewParticipationRequest;
import ru.practicum.explore.with.me.model.participation.ParticipationRequest;
import ru.practicum.explore.with.me.model.participation.ParticipationRequestDto;
import ru.practicum.explore.with.me.model.participation.ParticipationRequestStatus;
import ru.practicum.explore.with.me.model.user.User;
import ru.practicum.explore.with.me.repository.EventRepository;
import ru.practicum.explore.with.me.repository.ParticipationRequestRepository;
import ru.practicum.explore.with.me.repository.UserRepository;
import ru.practicum.explore.with.me.util.DataProvider;
import ru.practicum.explore.with.me.util.ExistenceValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ParticipationRequestServiceImpl implements ParticipationRequestService,
        ExistenceValidator<ParticipationRequest>, DataProvider<ParticipationRequestDto, ParticipationRequest> {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ExistenceValidator<User> userExistenceValidator;
    private final ExistenceValidator<Event> eventExistenceValidator;
    private final ParticipationRequestMapper participationRequestMapper;


    @Override
    public List<ParticipationRequestDto> find(Long userId) {
        userExistenceValidator.validateExists(userId);

        List<ParticipationRequestDto> result = participationRequestRepository.findAllByRequesterId(userId).stream()
                .map(this::getDto)
                .toList();
        log.info("ParticipationRequestServiceImpl: result of find(): {}", result);
        return result;
    }

    @Override
    @Transactional
    public ParticipationRequestDto create(NewParticipationRequest newParticipationRequest) {
        Long requesterId = newParticipationRequest.getUserId();
        Long eventId = newParticipationRequest.getEventId();
        eventExistenceValidator.validateExists(eventId);
        userExistenceValidator.validateExists(requesterId);

        Event event = eventRepository.findById(eventId).get();

        if (participationRequestRepository.existsByRequesterIdAndEventId(
                requesterId, eventId)) {
            log.info("attempt to create already existent participationRequest with requesterId: {}, eventId: {}",
                    requesterId, eventId);
            throw new ConflictException("Duplicate request.", "participationRequest with requesterId: " + requesterId +
                    ", and eventId: " + eventId + " already exists");
        }

        if (event.getInitiator().getId().equals(requesterId)) {
            log.info("attempt to create participationRequest by an event initiator with requesterId: {}, eventId: {}, " +
                    "initiatorId: {}", requesterId, eventId, event.getInitiator().getId());
            throw new ConflictException("Initiator can't create participation request.", "requesterId: "
                    + requesterId + " equals to initiatorId: " + event.getInitiator().getId());
        }

        if (event.getPublishedOn() == null) {
            log.info("attempt to create participationRequest for not published event with " +
                    "requesterId: {}, eventId: {}", requesterId, eventId);
            throw new ConflictException("Can't create participation request for unpublished event.",
                    "event with id: " + eventId + " is not published yet");
        }

        if (event.getParticipantLimit() <= participationRequestRepository.countByEventId(eventId)) {
            log.info("attempt to create participationRequest, but participantLimit: {} is reached",
                    event.getParticipantLimit());
            throw new ConflictException("Participant limit is reached.", "event with id: " + eventId +
                    " has participant limit of: " + event.getParticipantLimit());
        }

        ParticipationRequest request = mapEntity(newParticipationRequest);
        if (event.isRequestModeration()) {
            request.setStatus(ParticipationRequestStatus.CONFIRMED);
        }

        ParticipationRequestDto result = getDto(participationRequestRepository.save(request));
        log.info("ParticipationRequestServiceImpl: result of create():: {}", result);
        return result;
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(CancelParticipationRequest cancelParticipationRequest) {
        validateExists(cancelParticipationRequest.getRequestId());
        userExistenceValidator.validateExists(cancelParticipationRequest.getUserId());

        ParticipationRequestDto result = participationRequestMapper.toDto(
                participationRequestRepository.findById(cancelParticipationRequest.getRequestId()).get());
        participationRequestRepository.deleteById(cancelParticipationRequest.getRequestId());

        log.info("ParticipationRequestServiceImpl: result of cancel(): {}, which has been deleted", result);
        return result;
    }

    private ParticipationRequest mapEntity(NewParticipationRequest newParticipationRequest) {
        userExistenceValidator.validateExists(newParticipationRequest.getUserId());
        eventExistenceValidator.validateExists(newParticipationRequest.getEventId());

        ParticipationRequest entity = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .requester(userRepository.findById(newParticipationRequest.getUserId()).get())
                .event(eventRepository.findById(newParticipationRequest.getEventId()).get())
                .status(ParticipationRequestStatus.PENDING)
                .build();

        log.trace("ParticipationRequestServiceImpl: result of mapEntity(): {}", entity);
        return entity;
    }

    @Override
    public ParticipationRequestDto getDto(ParticipationRequest entity) {
        return participationRequestMapper.toDto(entity);
    }

    @Override
    public void validateExists(Long id) {
        if (participationRequestRepository.findById(id).isEmpty()) {
            log.info("attempt to find participationRequest with id: {}", id);
            throw new NotFoundException("The required object was not found.",
                    "ParticipationRequest with id=" + id + " was not found");
        }
    }
}
