package ru.practicum.explore.with.me.model.comment;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class CommentDto {
    private long id;
    private String text;
    private AuthorDto authorDto;
    private EventDto eventDto;
    private LocalDateTime createdOn;

    private record AuthorDto(long id, String name) {}
    private record EventDto(long id, String title) {}
}
