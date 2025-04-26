package tn.esprit.spring.forumservice.Service.API;

import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tn.esprit.spring.forumservice.Config.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceAPI {

    private final RestTemplate restTemplate;
    private final SightengineApiConfig sightengineApiConfig;
    private final PerspectiveApiConfig perspectiveApiConfig;
    private static final float TOXICITY_THRESHOLD = 0.7f;
    private final DeepAIConfig deepAIConfig;
    private final PredisAIConfig predisConfig;




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
    }



//public String generateImage(String textDescription) {
//    try {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("api-key", deepAIConfig.getApiKey());
//
//        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
//        requestBody.add("text", textDescription);
//
//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        Map<String, Object> response = restTemplate.postForObject(
//                deepAIConfig.getApiUrl() + "/text2img",
//                requestEntity,
//                Map.class
//        );
//
//        return (String) response.get("output_url");
//    } catch (Exception e) {
//        System.err.println("Error generating image: " + e.getMessage());
//        throw new RuntimeException("Failed to generate image: " + e.getMessage());
//    }
//}


/*
public String generateVideo(String textDescription) {
    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("api-key", deepAIConfig.getApiKey());

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("text", textDescription);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject(
                deepAIConfig.getApiUrl() + "/text2video",
                requestEntity,
                Map.class
        );

        return (String) response.get("output_url");
    } catch (Exception e) {
        System.err.println("Error generating video: " + e.getMessage());
        throw new RuntimeException("Failed to generate video: " + e.getMessage());
    }
}
*/

public String generateContentWithPredis(String prompt, String contentType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", predisConfig.getApiKey()); // No "Bearer" prefix

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("brand_id", "67ff8d8b65b3c658c22d35fc"); // Replace with actual brand ID
            requestBody.put("text", prompt);
            requestBody.put("media_type", contentType.equals("image") ? "single_image" : "video");

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            String apiUrl = "https://brain.predis.ai/predis_api/v1/create_content/"; // Correct endpoint

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Handle asynchronous nature of the API
                // This API doesn't return content URL directly, just post IDs
                List<String> postIds = (List<String>) response.getBody().get("post_ids");
                if (postIds != null && !postIds.isEmpty()) {
                    return "Content creation initiated with ID: " + postIds.get(0) +
                           " (Check your webhook for the final content URL)";
                } else {
                    throw new RuntimeException("No post IDs received from Predis.ai");
                }
            } else {
                throw new RuntimeException("Failed to generate content with Predis.ai: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error generating content with Predis.ai: " + e.getMessage());
            throw new RuntimeException("Failed to generate content: " + e.getMessage());
        }
    }
    public String generateImage(String prompt) {
        return generateContentWithPredis(prompt, "image");
    }

    public String generateVideo(String prompt) {
        return generateContentWithPredis(prompt, "video");
    }



    // Add these fields to ServiceAPI class
    private final HuggingFaceConfig huggingFaceConfig;

/**
 * Translates text using the NLLB-200 model which supports 200 languages
 * @param text Text to translate
 * @param targetLang Target language code
 * @return Translated text
 */
public String translate(String text, String targetLang) {
    try {
        // Map the target language to FLORES-200 code
        String targetCode = mapToNLLBLanguageCode(targetLang);
        String sourceLang = detectLanguage(text);
        String sourceCode = mapToNLLBLanguageCode(sourceLang);

        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", text);

        // Format parameters according to NLLB model requirements
        Map<String, String> parameters = new HashMap<>();
        parameters.put("src_lang", sourceCode);
        parameters.put("tgt_lang", targetCode);

        requestBody.put("parameters", parameters);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + huggingFaceConfig.getApiKey());

        // Create request entity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Make API call to the full URL with model name
        ResponseEntity<String> response = restTemplate.exchange(
                huggingFaceConfig.getNllbUrl(), // Use getNllbUrl() which includes the model path
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Parse response
        String responseBody = response.getBody();
        if (responseBody == null) {
            return text;
        }

        // Process JSON response - NLLB typically returns array with translation_text
        if (responseBody.startsWith("[")) {
            // Use ObjectMapper to parse the JSON array
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, String>> result = mapper.readValue(responseBody,
                    new TypeReference<List<Map<String, String>>>() {});
            if (!result.isEmpty() && result.get(0).containsKey("translation_text")) {
                return result.get(0).get("translation_text");
            }
        }

        return responseBody.replaceAll("^\"(.*)\"$", "$1");
    } catch (Exception e) {
        System.out.println("Error translating text: " + e.getMessage());
        // Return original text on error
        return text;
    }
}



/**
 * Maps language codes to FLORES-200 codes used by NLLB
 */
private String mapToNLLBLanguageCode(String lang) {
    // Common language mappings to FLORES codes
    Map<String, String> floresMap = new HashMap<>();
    floresMap.put("en", "eng_Latn");
    floresMap.put("fr", "fra_Latn");
    floresMap.put("ar", "arb_Arab");
    floresMap.put("es", "spa_Latn");
    floresMap.put("de", "deu_Latn");
    floresMap.put("it", "ita_Latn");
    floresMap.put("zh", "zho_Hans");
    floresMap.put("ja", "jpn_Jpan");
    floresMap.put("ko", "kor_Hang");
    floresMap.put("ru", "rus_Cyrl");

    // Add more mappings for other languages as needed

    return floresMap.getOrDefault(lang.toLowerCase(), "eng_Latn");
}

/**
 * Returns available languages for translation with their FLORES-200 codes
 */
public Map<String, String> getAvailableLanguages() {
    Map<String, String> languages = new HashMap<>();
    languages.put("eng_Latn", "English");
    languages.put("fra_Latn", "French");
    languages.put("arb_Arab", "Arabic");
    languages.put("spa_Latn", "Spanish");
    languages.put("deu_Latn", "German");
    languages.put("ita_Latn", "Italian");
    languages.put("zho_Hans", "Chinese (Simplified)");
    languages.put("jpn_Jpan", "Japanese");
    languages.put("kor_Hang", "Korean");
    languages.put("rus_Cyrl", "Russian");
    // Add more NLLB supported languages

    return languages;
}
// After
    /**
     * Detects the language of the given text.
     * Placeholder implementation returning "en" (English) by default.
     * Replace with actual language detection logic if needed.
     */
   private String detectLanguage(String text) {
       // Use Lingua library for language detection
       LanguageDetector detector = LanguageDetectorBuilder.fromAllLanguages().build();
       Language detectedLanguage = detector.detectLanguageOf(text);

       // Convert Lingua language to your language code format
       if (detectedLanguage != null) {
           return detectedLanguage.getIsoCode639_1().toString().toLowerCase();
       }

       return "en"; // Fallback to English
   }
}