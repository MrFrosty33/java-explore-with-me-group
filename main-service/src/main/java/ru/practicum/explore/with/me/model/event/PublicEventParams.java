package ru.practicum.explore.with.me.model.event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
public class PublicEventParams {
    private String text;
    private Set<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private EventPublicSort sort;
    private Integer from;
    private Integer size;
}
