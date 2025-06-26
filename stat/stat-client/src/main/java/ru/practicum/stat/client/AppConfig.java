package ru.practicum.stat.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {
    @Bean
    public RestClient restClient(@Value("${stat.server-url}") String serverUrl) {
        return RestClient.builder()
            .baseUrl(serverUrl)
            .build();
    }
}