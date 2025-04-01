package tn.esprit.spring.transportservice.services.IMPL;

import org.springframework.stereotype.Service;
import tn.esprit.spring.transportservice.entity.Vehicule;
import tn.esprit.spring.transportservice.repository.VehiculeRepository;
import tn.esprit.spring.transportservice.services.interfaces.IVehiculeService;

import java.util.List;

@Service
public class VehiculeService implements IVehiculeService {
    private final VehiculeRepository vehiculeRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    @Override
    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id).orElse(null);
    }

    @Override
    public Vehicule save(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public void deleteById(Long id) {
        vehiculeRepository.deleteById(id);
    }


    @Override
    public Vehicule update(Long id, Vehicule updatedVehicule) {
        Vehicule existingVehicule = vehiculeRepository.findById(id).orElse(null);

        if (existingVehicule != null) {
            // Update the properties of the existing vehicule
            existingVehicule.setType(updatedVehicule.getType());
            existingVehicule.setModele(updatedVehicule.getModele());
            existingVehicule.setDisponible(updatedVehicule.isDisponible());
            existingVehicule.setStatut(updatedVehicule.getStatut());
            existingVehicule.setLocalisation(updatedVehicule.getLocalisation());
            existingVehicule.setPrixParJour(updatedVehicule.getPrixParJour());
            existingVehicule.setNbPlace(updatedVehicule.getNbPlace());
            existingVehicule.setRating(updatedVehicule.getRating());
            existingVehicule.setImage(updatedVehicule.getImage());

            return vehiculeRepository.save(existingVehicule);
        }

        return null;
    }
}
