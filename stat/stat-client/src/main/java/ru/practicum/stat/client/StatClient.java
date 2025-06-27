package ru.practicum.stat.client;

import ru.practicum.stat.dto.EndpointHitCreate;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatClient {
    void createHit(EndpointHitCreate endpointHitCreate);

    ViewStats getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
