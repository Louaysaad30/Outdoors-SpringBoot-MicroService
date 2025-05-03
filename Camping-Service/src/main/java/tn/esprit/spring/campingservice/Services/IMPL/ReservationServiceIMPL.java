package tn.esprit.spring.campingservice.Services.IMPL;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.campingservice.Entity.*;
import tn.esprit.spring.campingservice.Repository.*;
import tn.esprit.spring.campingservice.Services.Interfaces.IReservationService;
import tn.esprit.spring.campingservice.Services.Interfaces.IUserService;
import tn.esprit.spring.campingservice.dto.UserDto;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservationServiceIMPL implements IReservationService {
    private LigneReservationServiceIMPL ligneReservationServiceIMPL;

    @Autowired
    private ReservationReository reservationRepository;

    private CentreCampingRepository centreCampingRepository;
    private LogementRepository logementRepository;
    private MaterielRepsoitory materielRepository;
    private LigneReservationRepository ligneReservationRepository;


    @Override
    public List<Reservation> retrieveAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation addReservation(Reservation reservation) {
        // 1. Save the reservation first to get an ID
        Reservation savedReservation = reservationRepository.save(reservation);

        // 2. For each ligne reservation, set the parent reservation reference
        if (savedReservation.getLignesReservation() != null) {
            for (LigneReservation ligne : savedReservation.getLignesReservation()) {
                ligne.setReservation(savedReservation);

            }
            savedReservation = reservationRepository.save(savedReservation);

        }

        // 3. The associations are managed by the persistence context
        return savedReservation;
    }

    @Override
    public Reservation updateReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation retrieveReservation(Long idReservation) {
        return reservationRepository.findById(idReservation).orElse(null);
    }

    @Override
    public List<Reservation> findReservationsByCentreId(Long centreId) {
        return reservationRepository.findByCentreIdCentre(centreId);
    }

    @Override
    public Map<String, Object> checkAvailability(Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> unavailableItems = new ArrayList<>();
        boolean isAvailable = true;

        // Extract request parameters
        Long centreId = Long.valueOf(request.get("centreId").toString());
        Date startDate = new Date(((Number) request.get("startDate")).longValue());
        Date endDate = new Date(((Number) request.get("endDate")).longValue());
        int nbrPersonnes = (Integer) request.get("nbrPersonnes");
        List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");

        // 1. Check center capacity
        CentreCamping centre = centreCampingRepository.findById(centreId)
                .orElseThrow(() -> new RuntimeException("Centre not found"));

        List<Reservation> overlappingReservations = reservationRepository
                .findByCentreIdCentreAndDateFinGreaterThanEqualAndDateDebutLessThanEqual(
                        centreId, startDate, endDate);

        int bookedCapacity = overlappingReservations.stream()
                .mapToInt(Reservation::getNbrPersonnes)
                .sum();

        int availableCapacity = centre.getCapcite() - bookedCapacity;

        if (nbrPersonnes > availableCapacity) {
            isAvailable = false;
            response.put("personCapacityExceeded", true);
            response.put("availableCapacity", availableCapacity);
        } else {
            response.put("personCapacityExceeded", false);
        }

        // 2. Check item availability
        for (Map<String, Object> item : items) {
            Long itemId = Long.valueOf(item.get("id").toString());
            String itemType = (String) item.get("type");
            int requestedQuantity = (Integer) item.get("quantity");

            if ("logement".equals(itemType)) {
                // Get the logement
                Logement logement = logementRepository.findById(itemId)
                        .orElseThrow(() -> new RuntimeException("Logement not found"));

                // Find overlapping reservations for this logement
                List<LigneReservation> bookedLines = ligneReservationRepository
                        .findByLogementIdLogementAndDateFinGreaterThanEqualAndDateDebutLessThanEqual(
                                itemId, startDate, endDate);

                int bookedQuantity = bookedLines.stream()
                        .mapToInt(LigneReservation::getQuantite)
                        .sum();

                int availableQuantity = logement.getQuantity() - bookedQuantity;

                if (requestedQuantity > availableQuantity) {
                    isAvailable = false;
                    Map<String, Object> unavailableItem = new HashMap<>();
                    unavailableItem.put("id", itemId);
                    unavailableItem.put("name", logement.getName());
                    unavailableItem.put("type", "logement");
                    unavailableItem.put("requestedQuantity", requestedQuantity);
                    unavailableItem.put("availableQuantity", availableQuantity);
                    unavailableItems.add(unavailableItem);
                }
            } else if ("materiel".equals(itemType)) {
                // Get the materiel
                Materiel materiel = materielRepository.findById(itemId)
                        .orElseThrow(() -> new RuntimeException("Materiel not found"));

                // Find overlapping reservations for this materiel
                List<LigneReservation> bookedLines = ligneReservationRepository
                        .findByMaterielIdMaterielAndDateFinGreaterThanEqualAndDateDebutLessThanEqual(
                                itemId, startDate, endDate);

                int bookedQuantity = bookedLines.stream()
                        .mapToInt(LigneReservation::getQuantite)
                        .sum();

                int availableQuantity = materiel.getQuantity() - bookedQuantity;

                if (requestedQuantity > availableQuantity) {
                    isAvailable = false;
                    Map<String, Object> unavailableItem = new HashMap<>();
                    unavailableItem.put("id", itemId);
                    unavailableItem.put("name", materiel.getName());
                    unavailableItem.put("type", "materiel");
                    unavailableItem.put("requestedQuantity", requestedQuantity);
                    unavailableItem.put("availableQuantity", availableQuantity);
                    unavailableItems.add(unavailableItem);
                }
            }
        }

        response.put("available", isAvailable);
        response.put("unavailableItems", unavailableItems);

        return response;
    }

    @Override
    public List<Reservation> findReservationsByClientId(Long idClient) {
        return reservationRepository.findByIdClient(idClient);
    }

    @Override
    @Transactional
    public void removeReservation(Long idReservation) {
        // Retrieve the reservation
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + idReservation));

        // Delete the associated ligne reservations first
        ligneReservationServiceIMPL.removeLigneReservationsByReservationId(idReservation);

        // Detach the reservation from CentreCamping if needed
        if (reservation.getCentre() != null) {
            CentreCamping centre = reservation.getCentre();
            centre.getReservations().remove(reservation);
            reservation.setCentre(null);
        }

        // Delete the reservation
        reservationRepository.delete(reservation);

        // Flush to ensure changes are committed
        reservationRepository.flush();
    }

    @Override
    public List<Reservation> findConfirmedReservationsByCentreId(Long centreId) {
        return reservationRepository.findConfirmedByCentreId(centreId);
    }

    @Override
    @Transactional
    public Reservation confirmReservation(Long idReservation) {
        Reservation reservation = reservationRepository.findById(idReservation)
            .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + idReservation));

        reservation.setConfirmed(true);
        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> findByClientIdAndPaidIsTrue(Long idClient) {
        return reservationRepository.findByIdClientAndIsConfirmedTrue(idClient);
    }


}
