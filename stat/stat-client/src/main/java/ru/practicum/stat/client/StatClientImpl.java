package ru.practicum.stat.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stat.dto.EndpointHitCreate;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
@Service
public class StatClientImpl implements StatClient {
    @Value("${stat.server-url}")
    private final String serverUrl;
    private final RestClient client;

    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(serverUrl)
                .build();
    }

    // Насчёт валидации я не уверен, стоит ли её делать в клиенте или же делегировать это на контроллер
    // с одной стороны, если здесь уже отсеивать, то не придётся делать лишних вызовов к серверу
    // с другой стороны, будто бы это должно быть работой контроллера.
    // Да и вообще, нужна ли валидация, если это внутренний сервис, запросы в который поступают только от main-service?
    // Можно просто понадеяться, что сюда поступают уже корректные данные и опустить валидацию, оставить только логи

    public void createHit(@Valid EndpointHitCreate endpointHitCreate) {
        log.trace("StatClient: createHit() call with endpointHitCreate body: {}", endpointHitCreate);

        String url = serverUrl + "/hit";

        ResponseEntity<String> result = client
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitCreate)
                .retrieve()
                .toEntity(String.class);

        if (!result.getStatusCode().is2xxSuccessful()) {
            log.info("StatClient: createHit() success with status: {}, body: {}, serverUrl: {}",
                    result.getStatusCode(), result.getBody(), url);
        } else {
            log.warn("StatClient: createHit() failure with status: {}, body: {}, serverUrl: {}",
                    result.getStatusCode(), result.getBody(), url);
        }
    }

    public ViewStats getStats(@NotBlank(message = "must not be null") LocalDateTime start,
                              @NotBlank(message = "must not be null") LocalDateTime end,
                              List<String> uris,
                              boolean unique) {
        log.trace("StatClient: getStats() call with params: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);

        String url = UriComponentsBuilder
                .fromHttpUrl(serverUrl)
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .toUriString();

        ResponseEntity<ViewStats> result = client
                .get()
                .uri(url)
                .retrieve()
                .toEntity(ViewStats.class);

        if (!result.getStatusCode().is2xxSuccessful()) {
            log.info("StatClient: getStats() success with status: {}, body: {}, serverUrl: {}",
                    result.getStatusCode(), result.getBody(), url);
        } else {
            log.warn("StatClient: getStats() failure with status: {}, body: {}, serverUrl: {}",
                    result.getStatusCode(), result.getBody(), url);
        }

        return result.getBody();
    }

}
