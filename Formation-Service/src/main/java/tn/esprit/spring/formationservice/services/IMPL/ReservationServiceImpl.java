package tn.esprit.spring.formationservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.formationservice.dto.ReservationRequest;
import tn.esprit.spring.formationservice.dto.ReservationResponse;
import tn.esprit.spring.formationservice.dto.UserDto;
import tn.esprit.spring.formationservice.dto.UserReservationResponse;
import tn.esprit.spring.formationservice.entity.Formation;
import tn.esprit.spring.formationservice.entity.Reservation;
import tn.esprit.spring.formationservice.enums.StatutReservation;
import tn.esprit.spring.formationservice.repository.FormationRepository;
import tn.esprit.spring.formationservice.repository.ReservationRepository;
import tn.esprit.spring.formationservice.services.interfaces.IReservationService;
import tn.esprit.spring.formationservice.services.interfaces.IUserServiceClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final FormationRepository formationRepository;
    private final IUserServiceClient userServiceClient; // 🆕 injecter feign
    @Override
    public Reservation addReservation(ReservationRequest request, Long userId) {
        // 🔥 Vérifier si l'utilisateur a déjà réservé la même formation
        Optional<Reservation> existingReservation = reservationRepository
                .findByParticipantIdAndFormationId(userId, request.getFormationId());

        if (existingReservation.isPresent()) {
            throw new RuntimeException("Vous avez déjà réservé cette formation !");
        }

        Formation formation = formationRepository.findById(request.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation non trouvée."));

        Reservation reservation = Reservation.builder()
                .participantId(userId) // 🔥 Ici on utilise l'ID du user connecté
                .statut(StatutReservation.EN_ATTENTE)
                .dateReservation(LocalDateTime.now())
                .formation(formation)
                .build();

        return reservationRepository.save(reservation);
    }


    @Override
    public List<ReservationResponse> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream().map(reservation -> {
            UserDto participant = null;
            try {
                participant = userServiceClient.getUserById(reservation.getParticipantId());
            } catch (Exception e) {
                System.err.println("Erreur récupération utilisateur pour reservation id=" + reservation.getId() + ": " + e.getMessage());
            }

            return ReservationResponse.builder()
                    .id(reservation.getId())
                    .participantNom(participant != null ? participant.getNom() : "Inconnu")
                    .participantPrenom(participant != null ? participant.getPrenom() : "")
                    .formationTitre(reservation.getFormation().getTitre())
                    .statut(String.valueOf(reservation.getStatut()))
                    .dateReservation(reservation.getDateReservation())
                    .build();
        }).toList();
    }

    @Override
    public List<ReservationResponse> getReservationsByParticipant(Long participantId) {
        List<Reservation> reservations = reservationRepository.findByParticipantId(participantId);

        return reservations.stream().map(reservation -> {
            UserDto participant = userServiceClient.getUserById(reservation.getParticipantId());

            return ReservationResponse.builder()
                    .id(reservation.getId())
                    .participantNom(participant.getNom())
                    .participantPrenom(participant.getPrenom())
                    .formationTitre(reservation.getFormation().getTitre())
                    .statut(String.valueOf(reservation.getStatut()))
                    .dateReservation(reservation.getDateReservation())
                    .build();
        }).toList();
    }
    @Override
    public List<UserReservationResponse> getUserReservations(Long userId) {
        List<Reservation> reservations = reservationRepository.findByParticipantId(userId);

        return reservations.stream()
                .map(res -> UserReservationResponse.builder()
                        .id(res.getId())
                        .statut(res.getStatut().name())
                        .dateReservation(res.getDateReservation())
                        .formation(UserReservationResponse.FormationSummary.builder()
                                .titre(res.getFormation().getTitre())
                                .imageUrl(res.getFormation().getImageUrl())
                                .prix(res.getFormation().getPrix())
                                .duree(res.getFormation().getDureePauseMinutes() != null
                                        ? res.getFormation().getDureePauseMinutes() + " min"
                                        : "Unknown")
                                .dateDebut(res.getFormation().getDateDebut())
                                .build())
                        .build()
                )
                .collect(Collectors.toList()); // 🔥 ici
    }

    @Override
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatut(StatutReservation.ANNULE);
        reservationRepository.save(reservation);
    }

}
