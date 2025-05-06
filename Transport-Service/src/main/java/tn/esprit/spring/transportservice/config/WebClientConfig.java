package tn.esprit.spring.transportservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:5006") // Your Python service URL
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}

