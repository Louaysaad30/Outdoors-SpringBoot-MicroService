package tn.esprit.spring.marketplaceservice.services.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:9096/user") // Remplacez l'URL par celle de votre microservice
public interface UserServiceClient {

    @GetMapping("/all")
    List<Object> getAllUsers();

    @GetMapping("/role/livreur")
    List<Object> getUsersByRoleLivreur();
}
