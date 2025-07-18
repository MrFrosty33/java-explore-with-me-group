package ru.practicum.explore.with.me.model.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.explore.with.me.model.event.Event;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationRequestDto {
    private Long id;

    private String title;

    private Boolean pinned;

    private List<Event> events;
}
