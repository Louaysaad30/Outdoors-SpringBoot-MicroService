package tn.esprit.spring.campingservice.Services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.campingservice.Entity.Reservation;
import tn.esprit.spring.campingservice.Services.Interfaces.IReservationService;
import tn.esprit.spring.campingservice.Services.Interfaces.IUserReservationService;
import tn.esprit.spring.campingservice.Services.Interfaces.IUserService;
import tn.esprit.spring.campingservice.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserReservationServiceIMPL implements IUserReservationService {
    private final IUserService userClient;
    private final IReservationService reservationService;



    @Override
    public List<UserDto> getUsersByCentreId(Long centreId) {
        // Get all reservations for the center
        List<Reservation> reservations = reservationService.findReservationsByCentreId(centreId);

        // Extract unique user IDs from reservations
        List<Long> userIds = reservations.stream()
                .map(Reservation::getIdClient)
                .distinct()
                .collect(Collectors.toList());

        // If no users found, return empty list
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Fetch user details for each ID
        List<UserDto> users = new ArrayList<>();
        for (Long userId : userIds) {
            try {
                UserDto user = userClient.getUserById(userId);
                if (user != null) {
                    users.add(user);
                }
            } catch (Exception e) {
                System.err.println("Error fetching user with ID " + userId + ": " + e.getMessage());
            }
        }

        return users;
    }
}
