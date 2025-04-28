package tn.esprit.spring.forumservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.spring.forumservice.entity.UserDtoForum;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/user/{id}")
    ResponseEntity<UserDtoForum> getUserById(@PathVariable Long id);
}