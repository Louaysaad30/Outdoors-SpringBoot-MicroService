package tn.esprit.spring.campingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CampingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampingServiceApplication.class, args);
    }

}
