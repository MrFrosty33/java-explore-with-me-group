package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stat.dto.EndpointHitCreate;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping
@RestController
@Slf4j
public class StatController {
    private final StatService statService;

    @PostMapping("hit")
    public void createHit(@RequestBody EndpointHitCreate hitCreate) {
        log.info("Create hit: {}", hitCreate);
        statService.saveHit(hitCreate);
    }

    @GetMapping("stats")
    public List<ViewStats> getStats(@RequestParam
                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam
                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Get stats: {}, {}, {}, {}", start, end, uris, unique);
        return statService.getStats(start, end, uris, unique);
    }

}
