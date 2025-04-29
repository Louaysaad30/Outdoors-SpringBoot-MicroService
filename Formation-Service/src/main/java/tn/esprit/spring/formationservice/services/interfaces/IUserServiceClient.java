package tn.esprit.spring.formationservice.services.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.spring.formationservice.dto.UserDto; // DTO local (simple)

@FeignClient(name = "USER-SERVICE")
public interface IUserServiceClient {

    @GetMapping("/user/{id}")
    UserDto getUserById(@PathVariable("id") Long userId);
}
