package tn.esprit.spring.transportservice.services.IMPL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final WebClient webClient;

    public GroqService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.groq.com/openai/v1").build();
    }

    public String generateVehiculeJson(Map<String, String> attributes) {
        String prompt = buildPrompt(attributes);

        Map<String, Object> requestBody = Map.of(
                "model", "llama3-70b-8192",
                "temperature", 0.8,
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful assistant that generates JSON data."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + groqApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String buildPrompt(Map<String, String> attrs) {
        return """
            Generate a JSON object representing a new Vehicule for the Outdoors platform.

            Required format:
            - type: %s
            - modele: %s
            - disponible: %s
            - statut: %s
            - localisation: %s
            - prixParJour: %s
            - nbPlace: %s
            - description: long, detailed, commercial-style, includes emojis
            Rules:
            - Do NOT include: rating, reviews, image
            - Return ONLY a valid JSON object
           
            """.formatted(
                attrs.getOrDefault("type", "VOITURE"),
                attrs.getOrDefault("modele", "Renault Clio"),
                attrs.getOrDefault("disponible", "true"),
                attrs.getOrDefault("statut", "DISPONIBLE"),
                attrs.getOrDefault("localisation", "Tunis"),
                attrs.getOrDefault("prixParJour", "50.0"),
                attrs.getOrDefault("nbPlace", "5")
        );
    }
}
