package tn.esprit.spring.eventservice.services.IMPL;

                import com.fasterxml.jackson.databind.JsonNode;
                import com.fasterxml.jackson.databind.ObjectMapper;
                import lombok.extern.slf4j.Slf4j;
                import org.springframework.beans.factory.annotation.Value;
                import org.springframework.http.HttpHeaders;
                import org.springframework.http.MediaType;
                import org.springframework.stereotype.Service;
                import org.springframework.web.reactive.function.client.WebClient;
                import tn.esprit.spring.eventservice.services.interfaces.IHuggingFaceService;

                import java.time.Duration;
                import java.util.*;

                @Service
                @Slf4j
                public class HuggingFaceServiceImpl implements IHuggingFaceService {
                    private boolean lastCallUsedFallback = false;
                    private final WebClient webClient;
                    private final ObjectMapper objectMapper = new ObjectMapper();

                    public HuggingFaceServiceImpl(@Value("${huggingface.api.token}") String apiToken) {
                        this.webClient = WebClient.builder()
                                .baseUrl("https://api-inference.huggingface.co/models")
                                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                                .build();
                    }

                    @Override
                    public String improveText(String originalText) {
                        lastCallUsedFallback = false;
                        if (originalText == null || originalText.trim().isEmpty()) {
                            return originalText;
                        }

                        try {
                            Map<String, Object> requestBody = Map.of("inputs", originalText);

                            log.info("Calling HuggingFace API to improve text");
                            String response = webClient.post()
                                    .uri("/facebook/bart-large-cnn")
                                    .bodyValue(requestBody)
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .timeout(Duration.ofSeconds(10))
                                    .block();

                            if (response != null) {
                                JsonNode jsonNode = objectMapper.readTree(response);
                                if (jsonNode.isArray() && jsonNode.size() > 0) {
                                    String improved = jsonNode.get(0).get("summary_text").asText();
                                    if (!improved.isEmpty()) {
                                        log.info("Successfully improved text using HuggingFace API");
                                        return improved;
                                    }
                                }
                            }

                            // API response was incomplete
                            log.error("API response was incomplete or invalid");
                            lastCallUsedFallback = true;
                            return originalText; // Return original text instead of using fallback
                        } catch (Exception e) {
                            lastCallUsedFallback = true;
                            log.error("Error improving text with HuggingFace API: {}", e.getMessage());
                            return originalText; // Return original text instead of using fallback
                        }
                    }

                    /*
                    private String improveTextFallback(String text) {
                        // Fallback implementation commented out
                    }
                    */

@Override
public String[] extractKeywords(String text) {
    lastCallUsedFallback = false;
    if (text == null || text.isEmpty()) {
        return new String[0];
    }

    try {
        Map<String, Object> requestBody = Map.of("inputs", text);

        log.info("Calling HuggingFace API to extract keywords");
        String response = webClient.post()
                .uri("/dslim/bert-base-NER")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .block();

        if (response != null) {
            log.debug("Raw API response: {}", response);
            JsonNode jsonNode = objectMapper.readTree(response);
            Set<String> uniqueEntities = new HashSet<>();

            if (jsonNode.isArray()) {
                // Process first-level array
                for (JsonNode entry : jsonNode) {
                    if (entry.isArray()) {
                        // Process nested arrays (BERT-NER format)
                        for (JsonNode item : entry) {
                            if (item.has("entity_group") && item.has("word")) {
                                String entityGroup = item.get("entity_group").asText();
                                String word = item.get("word").asText().replaceAll("##", "");

                                // Only collect meaningful named entities
                                if (word.length() > 1 && !word.matches("\\W+")) {
                                    uniqueEntities.add(word);
                                    log.debug("Found entity: {} of type {}", word, entityGroup);
                                }
                            }
                        }
                    }
                }
            }

            if (!uniqueEntities.isEmpty()) {
                log.info("Successfully extracted {} keywords using HuggingFace API", uniqueEntities.size());
                return uniqueEntities.toArray(new String[0]);
            } else {
                // If no entities were found through NER, extract simple keywords
                log.info("No named entities found, extracting simple keywords");
                String[] words = text.toLowerCase().split("\\s+");
                return Arrays.stream(words)
                        .filter(w -> w.length() > 4)
                        .filter(w -> !w.matches("\\W+"))
                        .limit(5)
                        .toArray(String[]::new);
            }
        }

        log.warn("Empty response from API");
        lastCallUsedFallback = true;
        return new String[0];
    } catch (Exception e) {
        lastCallUsedFallback = true;
        log.error("Error extracting keywords with HuggingFace API: {}", e.getMessage());
        return new String[0];
    }
}
                    /*
                    private String[] extractKeywordsFallback(String text) {
                        // Fallback implementation commented out
                    }
                    */

                    @Override
                    public boolean didLastCallUseFallback() {
                        return lastCallUsedFallback;
                    }
                }