package ru.practicum.explore.with.me.service.comment;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.with.me.exception.ForbiddenException;
import ru.practicum.explore.with.me.exception.NotFoundException;
import ru.practicum.explore.with.me.mapper.CommentMapper;
import ru.practicum.explore.with.me.model.comment.*;
import ru.practicum.explore.with.me.model.event.Event;
import ru.practicum.explore.with.me.model.user.User;
import ru.practicum.explore.with.me.repository.CommentRepository;
import ru.practicum.explore.with.me.repository.EventRepository;
import ru.practicum.explore.with.me.service.participation.request.ParticipationRequestService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class CommentServiceImpl implements CommentService {

    private static final String OBJECT_NOT_FOUND   = "Required object was not found.";
    private static final String CONDITIONS_NOT_MET = "Conditions are not met.";

    private final CommentRepository            repository;
    private final EventRepository              eventRepository;
    private final ParticipationRequestService  requestService;
    private final CommentMapper                mapper;



    // admin

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentDto getCommentById(Long id) {
        return mapper.toDto(getOrThrow(id));
    }

    @Override
    public void deleteCommentByAdmin(Long id) {
        repository.deleteById(id);
    }



    //private

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto dto) {
        validateText(dto.getText(), 100);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        OBJECT_NOT_FOUND,
                        String.format("Событие %d не найдено", eventId)
                ));

        if (event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Комментировать можно только прошедшие события");
        }
        if (!requestService.isParticipantApproved(userId, eventId)) {
            throw new ValidationException("Нужна одобренная заявка на участие");
        }

        Comment comment = mapper.toModel(dto);
        comment.setAuthor(new User(userId, null, null));
        comment.setEvent(event);

        return mapper.toDto(repository.save(comment));
    }

    @Override
    public CommentUpdateDto updateComment(Long userId, Long commentId, NewCommentDto dto) {
        validateText(dto.getText(), 1000);

        Comment comment = getOrThrow(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException(CONDITIONS_NOT_MET,
                    "Редактировать может только автор");
        }

        comment.setText(dto.getText());
        comment.setUpdatedOn(LocalDateTime.now());
        return mapper.toUpdateDto(comment);
    }

    @Override
    public void deleteCommentByAuthor(Long userId, Long commentId) {
        Comment comment = getOrThrow(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException(CONDITIONS_NOT_MET,
                    "Удалять может только автор");
        }
        repository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentUserDto> getCommentsByAuthor(Long userId, Pageable pageable) {
        return repository.findByAuthorIdOrderByCreatedOnDesc(userId, pageable)
                .stream()
                .map(mapper::toUserDto)
                .toList();
    }



    //public

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByEvent(Long eventId, Pageable pageable) {
        return repository.findByEventIdOrderByCreatedOnDesc(eventId, pageable)
                .stream()
                .map(mapper::toDto)
                .toList();
    }



    private void validateText(String text, int max) {
        if (text == null || text.isBlank() || text.length() > max) {
            throw new ValidationException(
                    String.format("Текст комментария должен быть 1–%d символов", max));
        }
    }

    private Comment getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        OBJECT_NOT_FOUND,
                        String.format("Комментарий %d не найден", id)
                ));
    }
}
