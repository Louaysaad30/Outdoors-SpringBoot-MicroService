package tn.esprit.spring.forumservice.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HuggingFaceConfig {

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Value("${huggingface.api.base-url:https://api-inference.huggingface.co/models}")
    private String baseUrl;

    @Value("${huggingface.api.model:facebook/nllb-200-distilled-600M}")
    private String model;

    public String getApiKey() {
        return apiToken;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getModel() {
        return model;
    }

    public String getNllbUrl() {
        return baseUrl + "/" + model;
    }
}