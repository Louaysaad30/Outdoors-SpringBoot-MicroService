package tn.esprit.spring.forumservice.Service.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.forumservice.Service.Interfaces.IUserService;
import tn.esprit.spring.forumservice.client.UserClient;
import tn.esprit.spring.forumservice.entity.UserDtoForum;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserClient userClient;
    @Override
    public UserDtoForum getUserDetails(Long userId) {
        return userClient.getUserById(userId).getBody();
    }
}