package tn.esprit.spring.userservice.Config;

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
                    "cloud_name", "dxhztswzh",
                    "api_key", "157593788438369",
                    "api_secret", "_PZgzgFZPLQ9Q2xuBakkvtqJfj8"
            ));
        }
    }