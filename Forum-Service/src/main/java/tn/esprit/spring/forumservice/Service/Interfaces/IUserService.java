package tn.esprit.spring.forumservice.Service.Interfaces;

import tn.esprit.spring.forumservice.entity.UserDtoForum;

public interface IUserService {
    UserDtoForum getUserDetails(Long userId);
}