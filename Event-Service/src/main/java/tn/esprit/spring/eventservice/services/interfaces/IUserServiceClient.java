package tn.esprit.spring.eventservice.services.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface IUserServiceClient {
    @GetMapping("/user/{id}")
    ResponseEntity<?> getUserById(@PathVariable("id") Long userId);

    default boolean userExists(Long userId) {
        try {
            ResponseEntity<?> response = getUserById(userId);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}