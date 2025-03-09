package tn.esprit.spring.campingservice.Services.IMPL;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.campingservice.Entity.Materiel;
import tn.esprit.spring.campingservice.Repository.MaterielRepsoitory;
import tn.esprit.spring.campingservice.Services.Interfaces.IMaterielService;

import java.util.List;

@Service
@AllArgsConstructor
public class MaterielServiceIMPL implements IMaterielService {
@Autowired
    private MaterielRepsoitory materielRepository;

    @Override
    public List<Materiel> retrieveAllMateriels() {
        return materielRepository.findAll();
    }

    @Override
    public Materiel addMateriel(Materiel materiel) {
        return materielRepository.save(materiel);
    }

    @Override
    public Materiel updateMateriel(Materiel materiel) {
        return materielRepository.save(materiel);
    }

    @Override
    public Materiel retrieveMateriel(Long idMateriel) {
        return materielRepository.findById(idMateriel).orElse(null);
    }

    @Override
    public void removeMateriel(Long idMateriel) {
        materielRepository.deleteById(idMateriel);
    }
}
