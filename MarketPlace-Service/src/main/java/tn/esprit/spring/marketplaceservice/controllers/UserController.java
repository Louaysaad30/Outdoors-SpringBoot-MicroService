package tn.esprit.spring.marketplaceservice.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.spring.marketplaceservice.services.IMPL.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public List<Object> getAllUsers() {
        return userService.fetchAllUsers();
    }
    @GetMapping("/role/livreur")
    public List<Object> getUsersByRoleLivreur() {
        return userService.getUsersByRoleLivreur();
    }
}