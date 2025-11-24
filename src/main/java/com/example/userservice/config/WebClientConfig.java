package com.example.userservice.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${time.worldtimeapi.url}")
    private String worldTimeApiBaseUrl;

    @Bean
    public WebClient worldTimeWebClient() {
        // configure buffer size larger if response is big (not necessary here)
        return WebClient.builder()
                .baseUrl(worldTimeApiBaseUrl)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }
}
