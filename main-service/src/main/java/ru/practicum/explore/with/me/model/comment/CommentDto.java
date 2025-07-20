package ru.practicum.explore.with.me.model.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private long id;
    private String text;
    private AuthorDto authorDto;
    private CommentEventDto eventDto;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    public static record AuthorDto(long id, String name) {
    }

    public static record CommentEventDto(long id, String title) {
    }
}
