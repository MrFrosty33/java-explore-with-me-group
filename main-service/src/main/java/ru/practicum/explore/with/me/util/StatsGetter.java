package ru.practicum.explore.with.me.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explore.with.me.model.event.dto.EventViewsParameters;
import ru.practicum.stat.client.StatClient;
import ru.practicum.stat.dto.ViewStats;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsGetter {
    private final StatClient statClient;

    public List<ViewStats> getEventViewStats(EventViewsParameters params) {
        return statClient.getStats(
                params.getStart(),
                params.getEnd(),
                params.getEventIdUris(),
                params.isUnique()).getBody();
    }
}
