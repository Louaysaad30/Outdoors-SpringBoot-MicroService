package tn.esprit.spring.forumservice.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.forumservice.Service.API.ServiceAPI;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/translation")
@RequiredArgsConstructor
public class TranslationController {
/*
    private final ServiceAPI serviceAPI;

    *//**
     * Translates text to the specified target language
     * @param requestBody Map containing 'text' to translate and 'targetLang' code
     * @return Translated text
     *//*
    @PostMapping("/translate")
    public ResponseEntity<Map<String, String>> translateText(@RequestBody Map<String, String> requestBody) {
        String text = requestBody.get("text");
        String targetLang = requestBody.get("targetLang");

        if (text == null || targetLang == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Both 'text' and 'targetLang' fields are required");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String translatedText = serviceAPI.translate(text, targetLang);

        Map<String, String> response = new HashMap<>();
        response.put("originalText", text);
        response.put("translatedText", translatedText);
        response.put("targetLanguage", targetLang);

        return ResponseEntity.ok(response);
    }

    *//**
     * Returns all available languages for translation
     * @return Map of language codes to language names
     *//*
    @GetMapping("/languages")
    public ResponseEntity<Map<String, String>> getAvailableLanguages() {
        return ResponseEntity.ok(serviceAPI.getAvailableLanguages());
    }*/
}