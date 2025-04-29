package tn.esprit.spring.formationservice.services.IMPL;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class YoutubeServiceImpl {

    @Value("${youtube.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create();

    public Mono<String> getVideoId(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://www.googleapis.com/youtube/v3/search"
                + "?part=snippet"
                + "&type=video"
                + "&maxResults=1"
                + "&q=" + encodedQuery
                + "&key=" + apiKey;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    JsonNode items = json.get("items");
                    if (items != null && items.size() > 0) {
                        return items.get(0).get("id").get("videoId").asText();
                    } else {
                        return null;
                    }
                });
    }
}
