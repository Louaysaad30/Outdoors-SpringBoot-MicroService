package tn.esprit.spring.marketplaceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MarketPlaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketPlaceServiceApplication.class, args);
    }

}
