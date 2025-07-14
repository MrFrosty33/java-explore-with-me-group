package ru.practicum.explore.with.me.service;

import ru.practicum.explore.with.me.model.event.EventFullDto;
import ru.practicum.explore.with.me.model.event.EventShortDto;
import ru.practicum.explore.with.me.model.event.NewEventDto;
import ru.practicum.explore.with.me.model.event.PublicEventParams;

import java.util.List;

public interface EventService {
    EventFullDto createEvent(long userId, NewEventDto eventDto);

    EventFullDto getEventById(long userId, long eventId);

    EventFullDto getPublicEventById(long eventId);

    List<EventShortDto> getPublicEvents(PublicEventParams params);
}
