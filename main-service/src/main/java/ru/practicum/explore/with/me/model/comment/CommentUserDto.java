package ru.practicum.explore.with.me.model.comment;

import java.time.LocalDateTime;

public class CommentUserDto {
    private long id;
    private String text;
    private EventDto eventDto;
    private LocalDateTime createdOn;

    private record EventDto(long id, String title) {}
}
