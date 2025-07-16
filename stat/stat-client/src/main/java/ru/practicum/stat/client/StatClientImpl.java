package ru.practicum.stat.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stat.dto.EndpointHitCreate;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Service
public class StatClientImpl implements StatClient {
    @Value("${stats.server-url}")
    private String serverUrl;
    private final RestClient client;

    public ResponseEntity<Void> createHit(EndpointHitCreate endpointHitCreate) {
        log.trace("StatClient: createHit() call with endpointHitCreate body: {}", endpointHitCreate);

        String url = serverUrl + "/hit";

        ResponseEntity<Void> result = client
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitCreate)
                .retrieve()
                .toEntity(Void.class);

        if (!result.getStatusCode().is2xxSuccessful()) {
            log.info("StatClient: createHit() success with status: {}, body: {}, serverUrl: {}",
                    result.getStatusCode(), result.getBody(), url);
        } else {
            log.warn("StatClient: createHit() failure with status: {}, serverUrl: {}",
                    result.getStatusCode(), url);
        }

        return result;
    }

    public ResponseEntity<List<ViewStats>> getStats(LocalDateTime start,
                                                    LocalDateTime end,
                                                    List<String> uris,
                                                    boolean unique) {
        log.trace("StatClient: getStats() call with params: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);
        uris.forEach(uri -> builder.queryParam("uris", uri));
        String url = builder.toUriString();

        ResponseEntity<List<ViewStats>> result = client
                .get()
                .uri(url)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ViewStats>>() {
                });

        if (result.getStatusCode().is2xxSuccessful()) {
            log.info("StatClient: getStats() success with status: {}, body: {}, serverUrl: {}",
                    result.getStatusCode(), result.getBody(), url);
        } else {
            log.warn("StatClient: getStats() failure with status: {}, body: {}, serverUrl: {}",
                    result.getStatusCode(), result.getBody(), url);
        }

        return result;
    }

}
