package tn.esprit.spring.campingservice.Services.Interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.spring.campingservice.dto.UserDto;

import java.util.List;

@FeignClient(name = "USER-SERVICE", url = "${user-service.url:http://mysql:9096}")
public interface IUserService {

    @GetMapping("/user/{id}")
    UserDto getUserById(@PathVariable("id") Long id);

}
