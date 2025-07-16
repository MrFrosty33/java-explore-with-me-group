package ru.practicum.explore.with.me.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.practicum.stat.client.StatClient;
import ru.practicum.stat.client.StatClientImpl;

@Configuration
public class StatClientConfig {
    @Bean
    public StatClient statClient(@Value("${stat.server-url}") String url, RestClient.Builder builder) {
        RestClient client = builder.baseUrl(url).build();
        return new StatClientImpl(url, client);
    }
}