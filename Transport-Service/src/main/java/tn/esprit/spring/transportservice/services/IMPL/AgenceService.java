package tn.esprit.spring.transportservice.services.IMPL;

import org.springframework.stereotype.Service;
import tn.esprit.spring.transportservice.entity.Agence;
import tn.esprit.spring.transportservice.repository.AgenceRepository;
import tn.esprit.spring.transportservice.services.interfaces.IAgenceService;

import java.util.List;

@Service
public class AgenceService implements IAgenceService {
    private final AgenceRepository agenceRepository;

    public AgenceService(AgenceRepository agenceRepository) {
        this.agenceRepository = agenceRepository;
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
        return agenceRepository.save(agence);
    }

    @Override
    public void deleteById(Long id) {
        agenceRepository.deleteById(id);
    }
}