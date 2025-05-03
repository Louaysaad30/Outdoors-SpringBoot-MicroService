package tn.esprit.spring.marketplaceservice.services.IMPL;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import tn.esprit.spring.marketplaceservice.services.interfaces.UserServiceClient;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserServiceClient userServiceClient;

    public List<Object> fetchAllUsers() {
        return userServiceClient.getAllUsers();
    }

    public List<Object> getUsersByRoleLivreur() {
        return userServiceClient.getUsersByRoleLivreur();
    }
}
