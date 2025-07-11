package ru.practicum.explore.with.me.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.with.me.exception.ConflictException;
import ru.practicum.explore.with.me.exception.NotFoundException;
import ru.practicum.explore.with.me.mapper.EventMapper;
import ru.practicum.explore.with.me.model.User;
import ru.practicum.explore.with.me.model.category.Category;
import ru.practicum.explore.with.me.model.event.Event;
import ru.practicum.explore.with.me.model.event.EventState;
import ru.practicum.explore.with.me.model.event.dto.EventFullDto;
import ru.practicum.explore.with.me.model.event.dto.EventShortDto;
import ru.practicum.explore.with.me.model.event.dto.EventViewsParameters;
import ru.practicum.explore.with.me.model.event.dto.NewEventDto;
import ru.practicum.explore.with.me.model.event.dto.UpdateEventUserAction;
import ru.practicum.explore.with.me.model.event.dto.UpdateEventUserRequest;
import ru.practicum.explore.with.me.repository.CategoryRepository;
import ru.practicum.explore.with.me.repository.EventRepository;
import ru.practicum.explore.with.me.repository.UserRepository;
import ru.practicum.explore.with.me.util.StatsGetter;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final StatsGetter statsGetter;

    @Transactional
    public EventFullDto createEvent(long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("The required object was not found.", "User with id=" + userId + " was not found"));

        long categoryId = eventDto.getCategory();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("The required object was not found.", "Category with id=" + categoryId + " was not found"));

        Event event = eventMapper.toModel(eventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        Event eventSaved = eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toFullDto(eventSaved);
        eventFullDto.setViews(0L);
        return eventFullDto;
    }

    public EventFullDto getEventById(long userId, long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("The required object was not found.", "User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("The required object was not found.", "Event with id=" + eventId + " was not found"));

        if (event.getInitiator().getId() != userId) {
            log.info("User with id {} doesn't have an event with id {}", userId, eventId);
            throw new ConflictException("The name of category should be unique.",
                    "Category with name=" + event.getCategory().getName() + " is already exist");
        }
        EventFullDto eventFullDto = eventMapper.toFullDto(event);

        EventViewsParameters statParams = EventViewsParameters.builder()
                .start(event.getCreatedOn()).end(LocalDateTime.now())
                .eventIds(List.of(event.getId())).unique(true)
                .build();
        Map<Long, Long> viewStats = getEventViews(statParams);
        eventFullDto.setViews(viewStats.getOrDefault(eventId, 0L));

        //request
        eventFullDto.setConfirmedRequests(0);
        return eventFullDto;
    }

    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEvent) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("The required object was not found.", "User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("The required object was not found.", "Event with id=" + eventId + " was not found"));

        if (event.getInitiator().getId() != userId) {
            log.info("User {} doesn't have an event with id {}", userId, eventId);
            throw new ConflictException("For the requested operation the conditions are not met.",
                    "Only initiator of event can it change");
        }

        if (event.getState() == EventState.PUBLISHED) {
            log.info("User {} cannot change an event {} with state PUBLISHED", userId, eventId);
            throw new ConflictException("For the requested operation the conditions are not met.",
                    "Only pending or canceled events can be changed");
        }

        if (updateEvent.getCategory() != null) {
            long categoryId = updateEvent.getCategory();
            Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                    new NotFoundException("The required object was not found.", "Category with id=" + categoryId + " was not found"));
            event.setCategory(category);
        }

        if (updateEvent.getStateAction() != null) {
            UpdateEventUserAction action = updateEvent.getStateAction();
            switch (action) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }

        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getEventDate() != null) {
            event.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(updateEvent.getLocation());
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toFullDto(event);
        EventViewsParameters statParams = EventViewsParameters.builder()
                .start(event.getCreatedOn()).end(LocalDateTime.now())
                .eventIds(List.of(event.getId())).unique(true)
                .build();

        Map<Long, Long> viewStats = getEventViews(statParams);
        eventFullDto.setViews(viewStats.getOrDefault(eventId, 0L));
        //request
        eventFullDto.setConfirmedRequests(0);

        return eventFullDto;
    }

    public List<EventShortDto> getEvents(long userId, int from, int count) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("The required object was not found.", "User with id=" + userId + " was not found"));
        Pageable pageable = PageRequest.of(from, count, Sort.by("createdOn").ascending());
        List<Event> events = eventRepository.findEventsByUser(user, pageable).getContent();
        List<Long> eventIds = events.stream().map(Event::getId).toList();
        EventViewsParameters params = EventViewsParameters.builder()
                .start(events.getFirst().getCreatedOn())
                .end(LocalDateTime.now())
                .eventIds(eventIds).unique(true).build();
        Map<Long, Long> viewStats = getEventViews(params);
        // confirmedRequest
        return events.stream().map(eventMapper::toShortDto).toList();
    }


    public Map<Long, Long> getEventViews(EventViewsParameters params) {
        List<ViewStats> stats = statsGetter.getEventViewStats(params);
        Map<Long, Long> views = new HashMap<>();
        if (stats != null) {
            for (ViewStats stat : stats) {
                Long eventId = extractId(stat.getUri());
                if (eventId != null) {
                    views.put(eventId, stat.getHits());
                }
            }
        }
        return views;
    }

    private Long extractId(String uri) {
        try {
            String[] parts = uri.split("/");
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }
}
