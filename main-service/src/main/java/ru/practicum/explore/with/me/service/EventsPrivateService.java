package ru.practicum.explore.with.me.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.with.me.mapper.EventMapper;
import ru.practicum.explore.with.me.model.Category;
import ru.practicum.explore.with.me.model.User;
import ru.practicum.explore.with.me.model.event.Event;
import ru.practicum.explore.with.me.model.event.EventFullDto;
import ru.practicum.explore.with.me.model.event.EventState;
import ru.practicum.explore.with.me.model.event.NewEventDto;
import ru.practicum.explore.with.me.repository.CategoryRepository;
import ru.practicum.explore.with.me.repository.EventPrivateRepository;
import ru.practicum.explore.with.me.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsPrivateService {
    private final EventPrivateRepository eventRepository;
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
}
