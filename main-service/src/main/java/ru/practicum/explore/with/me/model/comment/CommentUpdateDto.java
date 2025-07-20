package ru.practicum.explore.with.me.model.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentUpdateDto {
    private long id;
    private String text;
    private AuthorDto authorDto;
    private CommentEventDto eventDto;
    private LocalDateTime updatedOn;

    public static record AuthorDto(long id, String name) {
    }

    public static record CommentEventDto(long id, String title) {
    }
}
