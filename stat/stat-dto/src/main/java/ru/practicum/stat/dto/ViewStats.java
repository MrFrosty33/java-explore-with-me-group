package ru.practicum.stat.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
