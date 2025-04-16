package tn.esprit.spring.forumservice.Service.API;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tn.esprit.spring.forumservice.Config.PerspectiveApiConfig;
import tn.esprit.spring.forumservice.Config.SightengineApiConfig;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceAPI {

    private final RestTemplate restTemplate;
    private final SightengineApiConfig sightengineApiConfig;
    private final PerspectiveApiConfig perspectiveApiConfig;
    private static final float TOXICITY_THRESHOLD = 0.7f;



    public boolean isContentToxic(String content) {
        try {
            if (content == null || content.isEmpty()) {
                return false;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> comment = new HashMap<>();
            comment.put("text", content);
            requestBody.put("comment", comment);

            Map<String, Object> requestedAttributes = new HashMap<>();
            Map<String, Object> toxicity = new HashMap<>();
            toxicity.put("scoreThreshold", 0.0);
            requestedAttributes.put("TOXICITY", toxicity);

            requestBody.put("requestedAttributes", requestedAttributes);

            // Add API key to URL
            String url = perspectiveApiConfig.getApiUrl() + "?key=" + perspectiveApiConfig.getApiKey();

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Make API call
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

            // Process response
            if (response != null && response.containsKey("attributeScores")) {
                Map<String, Object> attributeScores = (Map<String, Object>) response.get("attributeScores");
                Map<String, Object> toxicityScore = (Map<String, Object>) attributeScores.get("TOXICITY");
                Map<String, Object> summaryScore = (Map<String, Object>) toxicityScore.get("summaryScore");
                double score = (double) summaryScore.get("value");

                return score >= TOXICITY_THRESHOLD;
            }

            return false;
        } catch (Exception e) {
            // Log the exception in a real application
            System.err.println("Error checking content toxicity: " + e.getMessage());
            return false;
        }
    }


    /**
     * Checks if image content is appropriate
     */
public boolean isImageAppropriate(String imageUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("api_user", sightengineApiConfig.getApiUser());
        map.add("api_secret", sightengineApiConfig.getApiSecret());
        map.add("url", imageUrl);
        map.add("models", "nudity-2.1,weapon,recreational_drug,medical,offensive-2.0,gore-2.0,text,violence,self-harm");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                sightengineApiConfig.getApiUrl(),
                request,
                Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> result = response.getBody();

            // Check for nudity content
            if (result.containsKey("nudity")) {
                Map<String, Object> nudity = (Map<String, Object>) result.get("nudity");
                if (nudity.containsKey("sexual_activity") && ((Double) nudity.get("sexual_activity")) > 0.4 ||
                    nudity.containsKey("sexual_display") && ((Double) nudity.get("sexual_display")) > 0.4 ||
                    nudity.containsKey("erotica") && ((Double) nudity.get("erotica")) > 0.4) {
                    return false;
                }
            }

            // Check for offensive content
            if (result.containsKey("offensive")) {
                Map<String, Object> offensive = (Map<String, Object>) result.get("offensive");
                if ((offensive.containsKey("nazi") && ((Double) offensive.get("nazi")) > 0.4) ||
                    (offensive.containsKey("supremacist") && ((Double) offensive.get("supremacist")) > 0.4) ||
                    (offensive.containsKey("terrorist") && ((Double) offensive.get("terrorist")) > 0.4) ||
                    (offensive.containsKey("middle_finger") && ((Double) offensive.get("middle_finger")) > 0.4)) {
                    return false;
                }
            }

            // Check for weapon content
            if (result.containsKey("weapon")) {
                Map<String, Object> weapon = (Map<String, Object>) result.get("weapon");
                if (weapon.containsKey("classes")) {
                    Map<String, Double> classes = (Map<String, Double>) weapon.get("classes");
                    if ((classes.containsKey("firearm") && classes.get("firearm") > 0.6) ||
                        (classes.containsKey("knife") && classes.get("knife") > 0.6)) {
                        return false;
                    }
                }
            }

            // Check for recreational drug content
            if (result.containsKey("recreational_drug")) {
                Map<String, Object> drug = (Map<String, Object>) result.get("recreational_drug");
                if (drug.containsKey("prob") && ((Double) drug.get("prob")) > 0.6) {
                    return false;
                }
            }

            // Check for gore content
            if (result.containsKey("gore")) {
                Map<String, Object> gore = (Map<String, Object>) result.get("gore");
                if (gore.containsKey("prob") && ((Double) gore.get("prob")) > 0.6) {
                    return false;
                }
            }

            // Check for violence content
            if (result.containsKey("violence")) {
                Map<String, Object> violence = (Map<String, Object>) result.get("violence");
                if (violence.containsKey("prob") && ((Double) violence.get("prob")) > 0.6) {
                    return false;
                }
            }

            // Check for self-harm content
            if (result.containsKey("self-harm")) {
                Map<String, Object> selfHarm = (Map<String, Object>) result.get("self-harm");
                if (selfHarm.containsKey("prob") && ((Double) selfHarm.get("prob")) > 0.5) {
                    return false;
                }
            }

            return true;
        }

        return true;
    }}