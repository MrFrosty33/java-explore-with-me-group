package ru.practicum.explore.with.me.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.with.me.exception.ConflictException;
import ru.practicum.explore.with.me.exception.NotFoundException;
import ru.practicum.explore.with.me.mapper.EventMapper;
import ru.practicum.explore.with.me.model.category.Category;
import ru.practicum.explore.with.me.model.event.Event;
import ru.practicum.explore.with.me.model.event.EventFullDto;
import ru.practicum.explore.with.me.model.event.EventState;
import ru.practicum.explore.with.me.model.event.NewEventDto;
import ru.practicum.explore.with.me.model.user.User;
import ru.practicum.explore.with.me.repository.CategoryRepository;
import ru.practicum.explore.with.me.repository.EventRepository;
import ru.practicum.explore.with.me.repository.UserRepository;
import ru.practicum.explore.with.me.util.ExistenceValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements ExistenceValidator<Event> {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventFullDto createEvent(long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("The required object was not found.",
                        "User with id=" + userId + " was not found"));
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("The required object was not found.",
                        "Category with id=" + eventDto.getCategory() + " was not found"));
        Event event = eventMapper.toModel(eventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        Event eventSaved = eventRepository.save(event);
        return eventMapper.toFullDto(eventSaved);
    }

    public EventFullDto getEventById(long userId, long eventId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("The required object was not found.",
                        "User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("The required object was not found.",
                        "Event with id=" + eventId + " was not found"));
        if (event.getInitiator().getId() != userId) {
            log.info("User with id {} doesn't have an event with id {}", userId, eventId);
            throw new ConflictException("The name of category should be unique.",
                    "Category with name=" + event.getCategory().getName() + " is already exist");
        }
        return eventMapper.toFullDto(event);
    }

    @Override
    public void validateExists(Long id) {
        if (eventRepository.findById(id).isEmpty()) {
            log.info("attempt to find event with id: {}", id);
            throw new NotFoundException("The required object was not found.",
                    "Event with id=" + id + " was not found");
        }
    }
}
