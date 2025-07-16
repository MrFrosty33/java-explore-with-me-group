package ru.practicum.explore.with.me.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.with.me.model.event.EventFullDto;
import ru.practicum.explore.with.me.model.event.EventPublicSort;
import ru.practicum.explore.with.me.model.event.EventShortDto;
import ru.practicum.explore.with.me.model.event.PublicEventParam;
import ru.practicum.explore.with.me.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventService eventsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) Set<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) EventPublicSort sort,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest request) {

        PublicEventParam publicEventParam = new PublicEventParam();
        publicEventParam.setText(text);
        publicEventParam.setCategories(categories);
        publicEventParam.setPaid(paid);
        publicEventParam.setRangeStart(rangeStart);
        publicEventParam.setRangeEnd(rangeEnd);
        publicEventParam.setOnlyAvailable(onlyAvailable);
        publicEventParam.setSort(sort);
        publicEventParam.setFrom(from);
        publicEventParam.setSize(size);
        log.info("Get all public events with params: {}", publicEventParam);

        return eventsService.getPublicEvents(publicEventParam);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable @Positive @NotNull Long eventId) {

        log.info("Get public event {}", eventId);
        return eventsService.getPublicEventById(eventId);

    }
}
