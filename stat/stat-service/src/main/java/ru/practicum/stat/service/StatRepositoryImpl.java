package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.practicum.stat.dto.EndpointHitCreate;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StatRepositoryImpl implements StatRepository {
    private final JdbcClient jdbcClient;

    private static final String SQL_BASE_ALL_HITS = """
            SELECT app, uri, COUNT(*) AS hits FROM hits
            WHERE created BETWEEN :start AND :end
            """;

    private static final String SQL_BASE_UNIQUE_HITS = """
            SELECT app, uri, COUNT(DISTINCT ip) AS hits FROM hits
            WHERE created BETWEEN :start AND :end
            """;

    private static final String SQL_GROUP_BY = " GROUP BY app, uri";

    @Override
    public void save(EndpointHitCreate hit) {
        jdbcClient.sql("""
                            INSERT INTO hits (app, uri, ip, created)
                            VALUES (?, ?, ?, ?)
                        """)
                .params(List.of(hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp()))
                .update();
    }

    @Override
    public List<ViewStats> findAllHits(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return fetchStats(SQL_BASE_ALL_HITS, start, end, uris);
    }

    @Override
    public List<ViewStats> findUniqueHits(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return fetchStats(SQL_BASE_UNIQUE_HITS, start, end, uris);
    }

    private List<ViewStats> fetchStats(String baseSql, LocalDateTime start, LocalDateTime end, List<String> uris) {
        StringBuilder sqlBuilder = new StringBuilder(baseSql);

        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);

        if (uris != null && !uris.isEmpty()) {
            sqlBuilder.append(" AND uri IN (:uris)");
            params.put("uris", uris);
        }

        sqlBuilder.append(SQL_GROUP_BY);

        return jdbcClient
                .sql(sqlBuilder.toString())
                .params(params)
                .query(ViewStats.class)
                .list();
    }
}
