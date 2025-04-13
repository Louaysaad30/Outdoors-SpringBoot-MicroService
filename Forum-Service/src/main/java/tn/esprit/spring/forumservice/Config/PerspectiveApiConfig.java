package tn.esprit.spring.forumservice.Config;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.client.RestTemplate;

    @Configuration
    public class PerspectiveApiConfig {

        @Value("${perspective.api.key}")
        private String apiKey;

        @Value("${perspective.api.url:https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze}")
        private String apiUrl;

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        public String getApiKey() {
            return apiKey;
        }

        public String getApiUrl() {
            return apiUrl;
        }
    }