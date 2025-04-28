package tn.esprit.spring.forumservice.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class DeepAIConfig {

    @Value("${deepai.api.key}")
    private String apiKey;

    @Value("${deepai.api.url}")
    private String apiUrl;
}