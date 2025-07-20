package ru.practicum.explore.with.me.model.comment;

import java.time.LocalDateTime;

public class CommentUpdateDto {
    private long id;
    private String text;
    private AuthorDto authorDto;
    private EventDto eventDto;
    private LocalDateTime updatedOn;

    private record AuthorDto(long id, String name) {}
    private record EventDto(long id, String title) {}
}
