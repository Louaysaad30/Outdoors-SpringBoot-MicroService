package tn.esprit.spring.campingservice.Services.IMPL;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.campingservice.Entity.CentreCamping;
import tn.esprit.spring.campingservice.Entity.Materiel;
import tn.esprit.spring.campingservice.Repository.MaterielRepsoitory;
import tn.esprit.spring.campingservice.Services.Interfaces.IMaterielService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class MaterielServiceIMPL implements IMaterielService {

    private MaterielRepsoitory materielRepository;
    private final Cloudinary cloudinary;


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

    @Override
    public List<Materiel> retrieveMaterielsByCentre(CentreCamping centre) {
        return materielRepository.findByCentre(centre);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }
}
