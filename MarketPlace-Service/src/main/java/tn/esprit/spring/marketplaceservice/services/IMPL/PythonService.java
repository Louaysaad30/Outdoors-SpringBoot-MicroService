package tn.esprit.spring.marketplaceservice.services.IMPL;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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

        Map<String, Object> payload = Map.of(
                "review_text", List.of(text),
                "date_de_naissance", List.of("2000-01-01"),  // Mettre une date fictive ou null
                "idReview", List.of(1)  // Mettre un ID fictif
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, List.class);
            List<Map<String, Object>> results = response.getBody();
            if (results != null && !results.isEmpty()) {
                return (String) results.get(0).get("sentiment_label");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "unknown";
    }



}