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
import org.springframework.web.util.UriBuilder;
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
        String url = serverUrl + "/hit";
        ResponseEntity<String> result = client
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitCreate)
                .retrieve()
                .toEntity(String.class);

        log.info("In StatClient result of createHit(): {} with body: {} from serverUrl: {}",
                result.getStatusCode(), result.getBody(), url);

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

        log.info("In StatClient result of getStats(): {} with body: {} from serverUrl: {}",
                result.getStatusCode(), result.getBody(), url);

        return result;
    }


}
