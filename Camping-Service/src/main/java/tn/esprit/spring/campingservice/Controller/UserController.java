package tn.esprit.spring.campingservice.Controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.campingservice.Services.IMPL.UserServiceIMPL;
import tn.esprit.spring.campingservice.dto.UserDto;

@RestController
@CrossOrigin("*")
@RequestMapping("/campinguser/users")
@AllArgsConstructor
public class UserController {
    private final UserServiceIMPL userService;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
