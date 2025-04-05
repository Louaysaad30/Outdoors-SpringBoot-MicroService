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

                        // List of models to try in order
                        List<String> models = Arrays.asList(
                            "yanekyuk/bert-uncased-keyword-extractor",
                            "ml6team/keyphrase-extraction-kbir-inspec",
                            "explosion/spacy-ner-bert-large"
                        );

                        for (String model : models) {
                            // Try each model with retries
                            for (int attempt = 0; attempt < 3; attempt++) {
                                try {
                                    // Add exponential backoff between retries
                                    if (attempt > 0) {
                                        Thread.sleep(1000 * (1 << (attempt - 1))); // 1s, 2s, 4s
                                    }

                                    Map<String, Object> requestBody = Map.of("inputs", text);

                                    log.info("Calling HuggingFace API model {} (attempt {}/3)", model, attempt + 1);
                                    String response = webClient.post()
                                            .uri("/" + model)
                                            .bodyValue(requestBody)
                                            .retrieve()
                                            .bodyToMono(String.class)
                                            .timeout(Duration.ofSeconds(30)) // Increased timeout
                                            .block();

                                    if (response != null) {
                                        log.info("Success! Got response from model {}", model);
                                        JsonNode jsonNode = objectMapper.readTree(response);
                                        Set<String> uniqueKeywords = new HashSet<>();

                                        // Process based on model-specific response format
                                        if (jsonNode.isArray()) {
                                            if (model.contains("bert-uncased-keyword")) {
                                                // For yanekyuk/bert-uncased-keyword-extractor
                                                for (JsonNode item : jsonNode) {
                                                    if (item.has("entity_group") && "KEYWORD".equals(item.get("entity_group").asText())) {
                                                        uniqueKeywords.add(item.get("word").asText().trim());
                                                    }
                                                }
                                            } else if (model.contains("keyphrase-extraction")) {
                                                // For ml6team/keyphrase-extraction-kbir-inspec
                                                for (JsonNode result : jsonNode) {
                                                    if (result.has("word")) {
                                                        uniqueKeywords.add(result.get("word").asText().trim());
                                                    }
                                                }
                                            } else {
                                                // For NER models like spacy-ner
                                                for (JsonNode item : jsonNode) {
                                                    if (item.has("entity") || item.has("entity_group")) {
                                                        String keyword = item.has("word") ? item.get("word").asText() :
                                                                        (item.has("token") ? item.get("token").asText() : "");
                                                        if (keyword != null && !keyword.trim().isEmpty()) {
                                                            uniqueKeywords.add(keyword.trim());
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (!uniqueKeywords.isEmpty()) {
                                            log.info("Extracted {} keywords using model: {}", uniqueKeywords.size(), model);
                                            return uniqueKeywords.toArray(new String[0]);
                                        }
                                    }
                                } catch (Exception e) {
                                    log.warn("Attempt {}/3 with model {} failed: {}",
                                          attempt + 1, model, e.getMessage());
                                }
                            }
                            log.warn("All attempts failed for model {}, trying next model", model);
                        }

                        log.error("All keyword extraction models failed");
                        return new String[0]; // Return empty array instead of using fallback
                    }


                    @Override
                    public boolean didLastCallUseFallback() {
                        return lastCallUsedFallback;
                    }


                    @Override
                    public byte[] generateImage(String prompt) {
                        if (prompt == null || prompt.trim().isEmpty()) {
                            return null;
                        }

                        try {
                            // Create a better prompt for event poster generation
                            String enhancedPrompt = "Professional event poster for: " + prompt;

                            Map<String, Object> requestBody = Map.of(
                                "inputs", enhancedPrompt,
                                "parameters", Map.of(
                                    "negative_prompt", "blurry, bad quality, distorted, poorly designed",
                                    "guidance_scale", 7.5,
                                    "height", 640,
                                    "width", 480
                                )
                            );

                            log.info("Calling HuggingFace API to generate image for event poster");
                            byte[] imageBytes = webClient.post()
                                    .uri("/stabilityai/stable-diffusion-xl-base-1.0")
                                    .bodyValue(requestBody)
                                    .retrieve()
                                    .bodyToMono(byte[].class)
                                    .timeout(Duration.ofSeconds(30))
                                    .block();

                            if (imageBytes != null && imageBytes.length > 0) {
                                log.info("Successfully generated image using HuggingFace API");
                                return imageBytes;
                            }

                            log.warn("Empty response from image generation API");
                            return null;
                        } catch (Exception e) {
                            log.error("Error generating image with HuggingFace API: {}", e.getMessage());
                            return null;
                        }
                    }
                }