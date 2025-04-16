package tn.esprit.spring.campingservice.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dsg0vy2z2",
                "api_key", "545359362719694",
                "api_secret", "5G8Nu1_f2YQ_6oBNWEic8HhC8FE"
        ));
    }
}