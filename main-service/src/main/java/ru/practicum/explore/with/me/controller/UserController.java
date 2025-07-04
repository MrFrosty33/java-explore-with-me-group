package ru.practicum.explore.with.me.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.with.me.model.user.NewUserRequest;
import ru.practicum.explore.with.me.model.user.UserDto;
import ru.practicum.explore.with.me.service.UserService;
import ru.practicum.stat.client.StatClient;
import ru.practicum.stat.dto.EndpointHitCreate;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Validated
public class UserController {
    private final UserService service;
    private final StatClient statClient;
    @Value("${app}")
    private String app;

    @GetMapping("/admin/users")
    public List<UserDto> find(@RequestParam
                              List<Long> ids,
                              @RequestParam(defaultValue = "0")
                              @PositiveOrZero(message = "must be positive or zero")
                              int from,
                              @RequestParam(defaultValue = "10")
                              @Positive(message = "must be positive")
                                  int size,
                              HttpServletRequest request) {
        saveStats(request);
        log.trace("UserController: find() call with ids: {}, from: {}, size: {}", ids, from, size);
        return service.find(ids, from, size);
    }

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody NewUserRequest newUserRequest,
                          HttpServletRequest request) {
        saveStats(request);
        log.trace("UserController: create() call with newUserRequest: {}", newUserRequest);
        return service.create(newUserRequest);
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable
                       @Positive(message = "must be positive")
                           Long userId,
                       HttpServletRequest request) {
        saveStats(request);
        log.trace("UserController: delete() call with userId: {}", userId);
        service.delete(userId);
    }

    private void saveStats(HttpServletRequest request) {
        EndpointHitCreate hitCreate = EndpointHitCreate.builder()
                .app(app)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        log.trace("UserController: saving stats with endpoint: {}", hitCreate.getUri());
        ResponseEntity<Void> statResult = statClient.createHit(hitCreate);
        if (!statResult.getStatusCode().is2xxSuccessful()) {
            log.trace("UserController: stats saved successfully");
        } else {
            log.trace("UserController: error acquired when saving stats");
        }
    }

}
