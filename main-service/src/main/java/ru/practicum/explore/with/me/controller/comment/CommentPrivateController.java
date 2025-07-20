package ru.practicum.explore.with.me.controller.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.with.me.model.comment.CommentDto;
import ru.practicum.explore.with.me.model.comment.CommentUpdateDto;
import ru.practicum.explore.with.me.model.comment.NewCommentDto;
import ru.practicum.explore.with.me.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @NotNull @PositiveOrZero Long userId,
                                    @RequestParam @NotNull @PositiveOrZero Long eventId,
                                    @RequestBody @Valid NewCommentDto commentDto) {
        log.info("Create comment {} for event {} by user {}", commentDto, eventId, userId);
        return new CommentDto();
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentUpdateDto updateComment(@PathVariable @NotNull @PositiveOrZero Long userId,
                                          @PathVariable @NotNull @PositiveOrZero Long commentId,
                                          @RequestBody @Valid NewCommentDto commentDto) {
        log.info("Update comment {} for event {} by user {}", commentDto, commentId, userId);
        return new CommentUpdateDto();
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @NotNull @PositiveOrZero Long userId,
                              @PathVariable @NotNull @PositiveOrZero Long commentId) {
        log.info("Delete comment {} by user {}", commentId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByUser (@PathVariable @NotNull @PositiveOrZero Long userId) {
        log.info("Get comments for user {}", userId);
        return List.of();
    }
}
