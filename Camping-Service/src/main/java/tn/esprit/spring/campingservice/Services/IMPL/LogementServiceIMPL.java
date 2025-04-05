package tn.esprit.spring.campingservice.Services.IMPL;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.campingservice.Entity.CentreCamping;
import tn.esprit.spring.campingservice.Entity.Logement;
import tn.esprit.spring.campingservice.Repository.LogementRepository;
import tn.esprit.spring.campingservice.Services.Interfaces.ILogementService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class LogementServiceIMPL implements ILogementService {

    private final Cloudinary cloudinary;
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

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    @Override
    public List<Logement> retrieveLogementsByCentre(CentreCamping centre) {
        return logementRepository.findByCentre(centre);
    }
}
