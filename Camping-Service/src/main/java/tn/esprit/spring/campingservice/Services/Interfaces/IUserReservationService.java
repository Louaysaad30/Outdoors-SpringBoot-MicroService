package tn.esprit.spring.campingservice.Services.Interfaces;

import tn.esprit.spring.campingservice.dto.UserDto;

import java.util.List;

public interface IUserReservationService {

    List<UserDto> getUsersByCentreId(Long centreId);

}
