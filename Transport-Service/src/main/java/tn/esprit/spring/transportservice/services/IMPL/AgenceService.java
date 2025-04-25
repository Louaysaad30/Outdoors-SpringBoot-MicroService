package tn.esprit.spring.transportservice.services.IMPL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.transportservice.entity.Agence;
import tn.esprit.spring.transportservice.entity.Vehicule;
import tn.esprit.spring.transportservice.repository.AgenceRepository;
import tn.esprit.spring.transportservice.repository.VehiculeRepository;
import tn.esprit.spring.transportservice.services.interfaces.IAgenceService;

import java.util.List;

@Service
public class AgenceService implements IAgenceService {

    private final AgenceRepository agenceRepository;
    private final VehiculeRepository vehiculeRepository;

    @Autowired
    public AgenceService(AgenceRepository agenceRepository, VehiculeRepository vehiculeRepository) {
        this.agenceRepository = agenceRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    public List<Agence> findAll() {
        return agenceRepository.findAll();
    }

    @Override
    public Agence findById(Long id) {
        return agenceRepository.findById(id).orElse(null);
    }

    @Override
    public Agence save(Agence agence) {
        Agence savedAgence = agenceRepository.save(agence);
        if (agence.getVehicules() != null) {
            for (Vehicule vehicule : agence.getVehicules()) {
                vehicule.setAgence(savedAgence);  // Set the agence for each vehicule
                vehiculeRepository.save(vehicule);  // Save the vehicule to persist the relationship
            }
        }

        return savedAgence;
    }

    @Override
    public Agence update(Long id, Agence agenceDetails) {
        Agence existingAgence = agenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agence not found"));
        existingAgence.setNom(agenceDetails.getNom());
        existingAgence.setEmail(agenceDetails.getEmail());
        existingAgence.setNumero(agenceDetails.getNumero());
        existingAgence.setAdresse(agenceDetails.getAdresse());
        return agenceRepository.save(existingAgence);
    }

    @Override
    public void deleteById(Long id) {
        agenceRepository.deleteById(id);
    }
}
