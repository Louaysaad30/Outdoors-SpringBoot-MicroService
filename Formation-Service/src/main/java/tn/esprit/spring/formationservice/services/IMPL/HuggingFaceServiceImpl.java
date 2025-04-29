// HuggingFaceServiceImpl.java
package tn.esprit.spring.formationservice.services.IMPL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tn.esprit.spring.formationservice.services.interfaces.IHuggingFaceService;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HuggingFaceServiceImpl implements IHuggingFaceService {

    private final WebClient primaryClient;
    private final WebClient fallbackClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HuggingFaceServiceImpl(@Value("${huggingface.api.token}") String apiToken) {
        this.primaryClient = WebClient.builder()
                .baseUrl("https://api-inference.huggingface.co/models/bigscience/bloomz-560m")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.fallbackClient = WebClient.builder()
                .baseUrl("https://api-inference.huggingface.co/models/HuggingFaceH4/zephyr-7b-beta")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    @Override
    public float[] computeSimilarity(String source, List<String> sentences) {
        Map<String, Object> payload = Map.of("inputs", Map.of(
                "source_sentence", source,
                "sentences", sentences
        ));

        try {
            String response = primaryClient.post()
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode node = objectMapper.readTree(response);
            if (node.isArray()) {
                float[] scores = new float[node.size()];
                for (int i = 0; i < node.size(); i++) {
                    scores[i] = (float) node.get(i).asDouble();
                }
                return scores;
            }
        } catch (Exception e) {
            log.error("Erreur pendant l'appel à HuggingFace (similarity)", e);
        }

        return new float[0];
    }

    @Override
    public String generateBetterDescription(String rawText) {
        String prompt = "[INST] Améliore cette description de formation en français et rends-la professionnelle : " + rawText + " [/INST]";
        Map<String, Object> payload = Map.of("inputs", prompt);

        try {
            String response = primaryClient.post()
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode node = objectMapper.readTree(response);
            if (node.isArray() && node.get(0).has("generated_text")) {
                return cleanOutput(node.get(0).get("generated_text").asText());
            }
        } catch (Exception e) {
            log.warn("Modèle principal indisponible, tentative de fallback...");
            return fallbackDescription(rawText);
        }

        return rawText;
    }

    public String fallbackDescription(String rawText) {
        Map<String, Object> payload = Map.of(
                "inputs", "Rédige une courte description de formation : " + rawText
        );

        try {
            String response = fallbackClient.post()
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode node = objectMapper.readTree(response);
            if (node.isArray() && node.get(0).has("generated_text")) {
                return cleanOutput(node.get(0).get("generated_text").asText());
            }
        } catch (Exception e) {
            log.error("Erreur fallback HF", e);
        }

        return rawText;
    }

    private String cleanOutput(String text) {
        return text.replaceAll("\\[/?INST]", "").replaceAll("(?i)ecris une courte description.*?:", "").trim();
    }

}