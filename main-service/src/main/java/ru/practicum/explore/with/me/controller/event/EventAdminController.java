package ru.practicum.explore.with.me.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.with.me.model.event.AdminEventFilter;
import ru.practicum.explore.with.me.model.event.dto.AdminEventSearchRequestDto;
import ru.practicum.explore.with.me.model.event.dto.EventFullDto;
import ru.practicum.explore.with.me.model.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.explore.with.me.service.event.EventAdminService;

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
