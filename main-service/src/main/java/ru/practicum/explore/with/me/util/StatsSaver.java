package ru.practicum.explore.with.me.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.stat.client.StatClient;
import ru.practicum.stat.dto.EndpointHitCreate;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsSaver {
    private final StatClient statClient;
    @Value("${app}")
    private String app;

    public void save(HttpServletRequest request, String controllerName) {
        EndpointHitCreate hitCreate = EndpointHitCreate.builder()
                .app(app)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        log.trace("{}: saving stats with endpoint: {}", controllerName, hitCreate.getUri());
        ResponseEntity<Void> statResult = statClient.createHit(hitCreate);
        if (!statResult.getStatusCode().is2xxSuccessful()) {
            log.trace("{}: stats saved successfully", controllerName);
        } else {
            log.trace("{}: error acquired when saving stats", controllerName);
        }
    }
}
