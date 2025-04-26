package tn.esprit.spring.formationservice.config;

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
                "cloud_name", "dw2rwwtwb",
                "api_key", "948253583682361",
                "api_secret", "0Ll6s_f6Hcb_1nsAa-upfxRjLEo"
        );
        return new Cloudinary(config);
    }
}
