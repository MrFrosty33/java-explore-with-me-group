package ru.practicum.explore.with.me.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.with.me.model.event.EventFullDto;
import ru.practicum.explore.with.me.model.event.EventPublicSort;
import ru.practicum.explore.with.me.model.event.EventShortDto;
import ru.practicum.explore.with.me.model.event.PublicEventParams;
import ru.practicum.explore.with.me.service.EventsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventsPublicController {
    private final EventsService eventsService;

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

        PublicEventParams publicEventParams = new PublicEventParams();
        publicEventParams.setText(text);
        publicEventParams.setCategories(categories);
        publicEventParams.setPaid(paid);
        publicEventParams.setRangeStart(rangeStart);
        publicEventParams.setRangeEnd(rangeEnd);
        publicEventParams.setOnlyAvailable(onlyAvailable);
        publicEventParams.setSort(sort);
        publicEventParams.setFrom(from);
        publicEventParams.setSize(size);
        publicEventParams.setIpAdr(request.getRemoteAddr());
        log.info("Get all public events with params: {}", publicEventParams);

        return eventsService.getPublicEvents(publicEventParams);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable @Positive @NotNull Long eventId) {

        log.info("Get public event {}", eventId);
        return eventsService.getPublicEventById(eventId);

    }
}
