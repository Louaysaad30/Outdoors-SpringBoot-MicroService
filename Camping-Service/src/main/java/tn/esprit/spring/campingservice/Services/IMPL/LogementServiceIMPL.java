package tn.esprit.spring.campingservice.Services.IMPL;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.campingservice.Entity.Logement;
import tn.esprit.spring.campingservice.Repository.LogementRepository;
import tn.esprit.spring.campingservice.Services.Interfaces.ILogementService;

import java.util.List;

@Service
@AllArgsConstructor
public class LogementServiceIMPL implements ILogementService {

        private LogementRepository logementRepository;

        @Override
        public List<Logement> retrieveAllLogements() {
            return logementRepository.findAll();
        }

        @Override
        public Logement addLogement(Logement logement) {
            return logementRepository.save(logement);
        }

        @Override
        public Logement updateLogement(Logement logement) {
            return logementRepository.save(logement);
        }

        @Override
        public Logement retrieveLogement(Long idLogement) {
            return logementRepository.findById(idLogement).orElse(null);
        }

        @Override
        public void removeLogement(Long idLogement) {
            logementRepository.deleteById(idLogement);
        }
}
