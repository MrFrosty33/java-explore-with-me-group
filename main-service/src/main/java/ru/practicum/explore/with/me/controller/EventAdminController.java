package ru.practicum.explore.with.me.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.with.me.model.event.*;
import ru.practicum.explore.with.me.model.event.dto.AdminEventSearchRequestDto;
import ru.practicum.explore.with.me.model.event.dto.EventFullDto;
import ru.practicum.explore.with.me.model.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.explore.with.me.service.EventAdminService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventAdminController {

    private final EventAdminService service;

    // GET /admin/events
    @GetMapping
    public List<EventFullDto> searchEvents(@ModelAttribute AdminEventSearchRequestDto req) {
        Pageable page = PageRequest.of(req.getFrom() / req.getSize(), req.getSize());
        AdminEventFilter f = new AdminEventFilter(
                req.getUsers(), req.getStates(), req.getCategories(),
                req.getRangeStart(), req.getRangeEnd());
        return service.search(f, page);
    }

    // PATCH /admin/events/{id}
    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody UpdateEventAdminRequestDto dto) {
        return service.update(eventId, dto);
    }
}
