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
}
