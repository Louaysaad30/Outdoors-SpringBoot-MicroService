package tn.esprit.spring.transportservice.services.interfaces;

import tn.esprit.spring.transportservice.entity.Agence;

import java.util.List;

public interface IAgenceService {
    List<Agence> findAll();

    Agence findById(Long id);

    Agence save(Agence agence);

    void deleteById(Long id);
}
