package tn.esprit.spring.transportservice.config;


import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "drdvbbr1d");
        config.put("api_key", "848374464672151");
        config.put("api_secret", "4Vxmnt7bITpi-trjT2HOT_Ju_0E");
        return new Cloudinary(config);
    }
}
