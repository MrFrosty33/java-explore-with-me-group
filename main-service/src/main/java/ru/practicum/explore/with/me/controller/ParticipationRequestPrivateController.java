package ru.practicum.explore.with.me.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.with.me.model.participation.CancelParticipationRequest;
import ru.practicum.explore.with.me.model.participation.NewParticipationRequest;
import ru.practicum.explore.with.me.model.participation.ParticipationRequestDto;
import ru.practicum.explore.with.me.service.ParticipationRequestService;

import java.util.List;

@RestController("/users/{userId}/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Validated
public class ParticipationRequestPrivateController {
    private final ParticipationRequestService service;
    private final String controllerName = "ParticipationRequestPrivateController";

    @GetMapping
    public List<ParticipationRequestDto> find(@PathVariable
                                              @NotNull(message = "must not be null")
                                              @Positive(message = "must be positive")
                                              Long userId,
                                              HttpServletRequest httpServletRequest) {
        log.trace("{}: find() call with userId: {}", controllerName, userId);
        return service.find(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable
                                          @NotNull(message = "must not be null")
                                          @Positive(message = "must be positive")
                                          Long userId,
                                          @RequestParam
                                          @NotNull(message = "must not be null")
                                          @Positive(message = "must be positive")
                                          Long eventId,
                                          HttpServletRequest httpServletRequest) {
        log.trace("{}: create() call with userId: {}, eventId: {}", controllerName, userId, eventId);

        NewParticipationRequest newParticipationRequest = NewParticipationRequest.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
        return service.create(newParticipationRequest);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable
                                          @NotNull(message = "must not be null")
                                          @Positive(message = "must be positive")
                                          Long userId,
                                          @PathVariable
                                          @NotNull(message = "must not be null")
                                          @Positive(message = "must be positive")
                                          Long requestId, HttpServletRequest httpServletRequest) {
        log.trace("{}: cancel() call with userId: {}, requestId: {}", controllerName, userId, requestId);

        CancelParticipationRequest cancelParticipationRequest = CancelParticipationRequest.builder()
                .userId(userId)
                .requestId(requestId)
                .build();
        return service.cancel(cancelParticipationRequest);
    }
}
