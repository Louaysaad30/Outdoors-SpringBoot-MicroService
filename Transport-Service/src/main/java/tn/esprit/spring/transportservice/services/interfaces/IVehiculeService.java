package tn.esprit.spring.transportservice.services.interfaces;

import tn.esprit.spring.transportservice.entity.Vehicule;

import java.util.List;

public interface IVehiculeService {

    List<Vehicule> findAll();
    Vehicule findById(Long id);
    Vehicule save(Vehicule vehicule);
    void deleteById(Long id);

    Vehicule update(Long id, Vehicule updatedVehicule);
}
