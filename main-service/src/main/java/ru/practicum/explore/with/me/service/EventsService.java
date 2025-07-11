package ru.practicum.explore.with.me.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.with.me.exception.NotFoundException;
import ru.practicum.explore.with.me.mapper.EventMapper;
import ru.practicum.explore.with.me.model.Category;
import ru.practicum.explore.with.me.model.User;
import ru.practicum.explore.with.me.model.event.*;
import ru.practicum.explore.with.me.repository.CategoryRepository;
import ru.practicum.explore.with.me.repository.EventRepository;
import ru.practicum.explore.with.me.repository.UserRepository;
import ru.practicum.explore.with.me.exception.BadRequestException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventFullDto createEvent(long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId).orElse(null);
        //.orElseThrow(
        // () -> new NotFoundException("The required object was not found.",
        //        "User with id=" + id + " was not found");
        //);
        Category category = categoryRepository.findById(eventDto.getCategory()).orElse(null);
        //.orElseThrow(
        // () -> new NotFoundException("The required object was not found.",
        //        "Category with id=" + id + " was not found");
        //);
        Event event = eventMapper.toModel(eventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        Event eventSaved = eventRepository.save(event);
        return eventMapper.toFullDto(eventSaved);
    }

    public EventFullDto getEventById(long userId, long eventId) {
        User user = userRepository.findById(userId).orElse(null);
        //.orElseThrow(
        // () -> new NotFoundException("The required object was not found.",
        //        "User with id=" + id + " was not found");
        //);
        Event event = eventRepository.findById(eventId).orElse(null);
        //.orElseThrow(
        // () -> new NotFoundException("The required object was not found.",
        //        "Event with id=" + id + " was not found");
        //);
        if (event.getInitiator().getId() != userId) {
            log.info("User with id {} doesn't have an event with id {}", userId, eventId);
            //throw new ConflictException("The name of category should be unique.",
            //          "Category with name=" + name + " is already exist");
        }
        return eventMapper.toFullDto(event);
    }

    public EventFullDto getPublicEventById(long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, "published")
                .orElseThrow(() -> new NotFoundException("The required object was not found.",
                        "Event with id=" + eventId + " was not found"));

        return eventMapper.toFullDto(event);
    }

    public List<EventShortDto> getPublicEvents(PublicEventParams params) {
        if (params.getRangeStart() != null && params.getRangeEnd() != null
                && params.getRangeStart().isAfter(params.getRangeEnd())) {
            throw new BadRequestException("Start date must be before end date",
                    "Start: " + params.getRangeStart() + " End: " + params.getRangeEnd());
        }

        Pageable pageable = PageRequest.of(
                params.getFrom() / params.getSize(),
                params.getSize(),
                getSort(params.getSort())
        );

        // Get events from repository
        Page<Event> page = eventRepository.findPublicEvents(params, pageable);

        return page.getContent()
                .stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());

    }

    private Sort getSort(EventPublicSort sort) {
        if (sort == null) return Sort.unsorted();
        return switch (sort) {
            case EVENT_DATE -> Sort.by("eventDate").ascending();
            case VIEWS -> Sort.by("views").descending();
        };
    }
}
