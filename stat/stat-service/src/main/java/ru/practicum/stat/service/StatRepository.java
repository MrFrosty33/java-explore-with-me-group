package ru.practicum.stat.service;

import ru.practicum.stat.dto.EndpointHitCreate;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository {
    void save(EndpointHitCreate hit);

    List<ViewStats> findAllHits(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<ViewStats> findUniqueHits(LocalDateTime start, LocalDateTime end, List<String> uris);
}
