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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.with.me.model.event.EventFullDto;
import ru.practicum.explore.with.me.model.event.NewEventDto;
import ru.practicum.explore.with.me.service.EventsService;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventsPrivateController {
    private final EventsService eventsService;

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
                                     @PathVariable @Positive @NotNull Long eventId) {
        log.info("Get event {}", eventId);
        return eventsService.getEventById(userId, eventId);
    }

}
