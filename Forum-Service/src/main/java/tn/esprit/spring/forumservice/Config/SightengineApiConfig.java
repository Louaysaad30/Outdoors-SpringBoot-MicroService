package tn.esprit.spring.forumservice.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SightengineApiConfig {

    @Value("${sightengine.api.user}")
    private String apiUser;

    @Value("${sightengine.api.secret}")
    private String apiSecret;

    @Value("${sightengine.api.url:https://api.sightengine.com/1.0/check.json}")
    private String apiUrl;



    public String getApiUser() {
        return apiUser;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}