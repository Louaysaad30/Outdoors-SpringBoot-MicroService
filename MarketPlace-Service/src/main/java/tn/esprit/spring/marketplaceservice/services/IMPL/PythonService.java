package tn.esprit.spring.marketplaceservice.services.IMPL;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PythonService {

    public Object analyzeReviews(Map<String, Object> payload) {
        String url = "http://127.0.0.1:5000/analyze";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Python service error: " + e.getMessage(), e);
        }
    }

    public String analyzeSentiment(String text) {
        String url = "http://127.0.0.1:5000/analyze";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = Map.of("review_text", text);  // Assurez-vous que le payload utilise la bonne clé
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            // Récupérer la valeur du sentiment à partir de la clé "sentiment_label"
            return (String) responseBody.get("sentiment_label");  // Utilisez "sentiment_label" au lieu de "sentiment"
        } catch (Exception e) {
            return "unknown";
        }
    }


}