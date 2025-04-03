package tn.esprit.spring.userservice.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class webConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        registry.addMapping("/**").allowedOrigins("http://localhost:4200");
        registry.addMapping("/greeting-javaconfig").allowedOrigins("http://localhost:8084");

    }
}