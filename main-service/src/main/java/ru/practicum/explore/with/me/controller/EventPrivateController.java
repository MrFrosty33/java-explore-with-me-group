package ru.practicum.explore.with.me.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.with.me.model.event.dto.EventFullDto;
import ru.practicum.explore.with.me.model.event.dto.EventShortDto;
import ru.practicum.explore.with.me.model.event.dto.NewEventDto;
import ru.practicum.explore.with.me.model.event.dto.UpdateEventUserRequest;
import ru.practicum.explore.with.me.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService eventsService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @PositiveOrZero @NotNull Long userId,
                                    @RequestBody @Valid NewEventDto event) {
        log.info("Create event {}", event);
        return eventsService.createEvent(userId, event);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable @PositiveOrZero @NotNull Long userId,
                                     @PathVariable @PositiveOrZero @NotNull Long eventId) {
        log.info("Get event {}", eventId);
        return eventsService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable @PositiveOrZero @NotNull Long userId,
                                    @PathVariable @PositiveOrZero @NotNull Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEvent) {
        log.info("Update event {}", eventId);
        return eventsService.updateEvent(userId, eventId, updateEvent);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@PathVariable @PositiveOrZero @NotNull Long userId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get events by user {}", userId);
        return eventsService.getEvents(userId, from, size);
    }
}
