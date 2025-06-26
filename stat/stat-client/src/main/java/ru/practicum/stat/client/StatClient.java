package ru.practicum.stat.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stat.dto.model.EndpointHitCreate;
import ru.practicum.stat.dto.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping
@Validated
@Slf4j
public class StatClient {
    @Value("${stat.server-url}")
    private final String serverUrl;
    private final RestClient client;

    @PostMapping("/hit")
    public ResponseEntity<String> createHit(@RequestBody EndpointHitCreate endpointHitCreate) {
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

        return result;
    }

    @GetMapping("/stats")
    public ResponseEntity<ViewStats> getStats(@RequestParam
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                              LocalDateTime start,
                                              @RequestParam
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                              LocalDateTime end,
                                              @RequestParam(required = false) List<String> uris,
                                              @RequestParam(defaultValue = "false") boolean unique) {
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

        return result;
    }

}
