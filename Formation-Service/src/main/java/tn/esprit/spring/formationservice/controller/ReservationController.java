package tn.esprit.spring.formationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.formationservice.entity.Reservation;
import tn.esprit.spring.formationservice.services.interfaces.IReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservationController {

    private final IReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> add(@RequestBody Reservation reservation) {
        return new ResponseEntity<>(reservationService.addReservation(reservation), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAll() {
        return new ResponseEntity<>(reservationService.getAllReservations(), HttpStatus.OK);
    }

    @GetMapping("/participant/{id}")
    public ResponseEntity<List<Reservation>> getByParticipant(@PathVariable Long id) {
        return new ResponseEntity<>(reservationService.getReservationsByParticipant(id), HttpStatus.OK);
    }
}
