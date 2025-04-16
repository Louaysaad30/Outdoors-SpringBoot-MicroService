package tn.esprit.spring.transportservice.services.IMPL;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.transportservice.entity.Vehicule;
import tn.esprit.spring.transportservice.repository.VehiculeRepository;
import tn.esprit.spring.transportservice.services.interfaces.IVehiculeService;

import java.util.List;
import java.util.Map;

@Service
public class VehiculeService implements IVehiculeService {
    private final VehiculeRepository vehiculeRepository;

    @Autowired
    private Cloudinary cloudinary;

    public VehiculeService(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    public List<Vehicule> getVehiculesByAgence(Long agenceId) {
        return vehiculeRepository.findByAgenceId(agenceId);
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


    public Vehicule addVehiculeWithImage(Vehicule vehicule, MultipartFile imageFile)  {
        try {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
            System.out.println("Upload result: " + uploadResult);
            vehicule.setImage((String) uploadResult.get("secure_url"));
            return vehiculeRepository.save(vehicule);
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

}
