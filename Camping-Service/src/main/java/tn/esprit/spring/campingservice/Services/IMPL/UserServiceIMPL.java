package tn.esprit.spring.campingservice.Services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.campingservice.Entity.Reservation;
import tn.esprit.spring.campingservice.Services.Interfaces.IReservationService;
import tn.esprit.spring.campingservice.Services.Interfaces.IUserService;
import tn.esprit.spring.campingservice.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceIMPL {

    private final IUserService userClient;

    public UserDto getUserById(Long id) {
        return userClient.getUserById(id);
    }

}
