package ru.practicum.explore.with.me.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.with.me.model.event.dto.EventFullDto;
import ru.practicum.explore.with.me.model.event.dto.EventShortDto;
import ru.practicum.explore.with.me.model.event.dto.EventViewsParameters;
import ru.practicum.explore.with.me.model.event.dto.NewEventDto;
import ru.practicum.explore.with.me.model.event.dto.UpdateEventUserRequest;

import java.util.List;
import java.util.Map;

public interface EventService {
    @Transactional
    EventFullDto createEvent(long userId, NewEventDto eventDto);

    EventFullDto getEventById(long userId, long eventId);

    @Transactional
    EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEvent);

    EventFullDto getPublicEventById(long eventId);

    List<EventShortDto> getEvents(long userId, int from, int count);

    Map<Long, Long> getEventViews(EventViewsParameters params);

    List<EventShortDto> getPublicEvents(PublicEventParams params);
}
