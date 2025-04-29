package tn.esprit.spring.forumservice.Config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "predis")
@Getter
@Setter
public class PredisAIConfig {
    private String apiKey;
    private String apiUrl;


}