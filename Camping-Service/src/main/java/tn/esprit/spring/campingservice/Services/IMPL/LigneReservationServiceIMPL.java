package tn.esprit.spring.campingservice.Services.IMPL;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.campingservice.Entity.LigneReservation;
import tn.esprit.spring.campingservice.Repository.LigneReservationRepository;
import tn.esprit.spring.campingservice.Services.Interfaces.ILigneReservationService;

import java.util.List;

@Service
@AllArgsConstructor
public class LigneReservationServiceIMPL implements ILigneReservationService {

    private LigneReservationRepository ligneReservationRepository;

    @Override
    public List<LigneReservation> retrieveAllLigneReservations() {
        return ligneReservationRepository.findAll();
    }

    @Override
    public LigneReservation addLigneReservation(LigneReservation ligneReservation) {
        return ligneReservationRepository.save(ligneReservation);
    }

    @Override
    public LigneReservation updateLigneReservation(LigneReservation ligneReservation) {
        return ligneReservationRepository.save(ligneReservation);
    }

    @Override
    public LigneReservation retrieveLigneReservation(Long idLigne) {
        return ligneReservationRepository.findById(idLigne).orElse(null);
    }

    @Override
    public void removeLigneReservation(Long idLigne) {
        ligneReservationRepository.deleteById(idLigne);
    }



    // Add this new method
    public void removeLigneReservationsByReservationId(Long idReservation) {
        List<LigneReservation> lignes = ligneReservationRepository.findByReservationIdReservation(idReservation);
        if (!lignes.isEmpty()) {
            ligneReservationRepository.deleteAll(lignes);
        }
    }

    public LigneReservation updateLigneReservationByReservationId(Long idReservation, LigneReservation updatedLigneReservation) {
        LigneReservation existingLigneReservation = ligneReservationRepository.findByReservationIdReservation(idReservation)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("LigneReservation not found for reservation ID: " + idReservation));

        // Update fields
        existingLigneReservation.setDateDebut(updatedLigneReservation.getDateDebut());
        existingLigneReservation.setDateFin(updatedLigneReservation.getDateFin());
        existingLigneReservation.setQuantite(updatedLigneReservation.getQuantite());
        existingLigneReservation.setPrix(updatedLigneReservation.getPrix());

        return ligneReservationRepository.save(existingLigneReservation);
    }
}
