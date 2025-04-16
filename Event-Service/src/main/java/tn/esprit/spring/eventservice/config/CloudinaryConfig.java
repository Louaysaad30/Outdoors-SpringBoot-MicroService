package tn.esprit.spring.eventservice.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", "drjliiuy6",
                "api_key", "196838549891682",
                "api_secret", "a5r8QvtbPvlYYI-2jfRrAxfgtA8"
        );
        return new Cloudinary(config);
    }
}