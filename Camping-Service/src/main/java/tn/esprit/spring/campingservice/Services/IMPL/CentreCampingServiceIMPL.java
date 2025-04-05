package tn.esprit.spring.campingservice.Services.IMPL;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.campingservice.Entity.CentreCamping;
import tn.esprit.spring.campingservice.Repository.CentreCampingRepository;
import tn.esprit.spring.campingservice.Services.Interfaces.ICentreCampingService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CentreCampingServiceIMPL implements ICentreCampingService {


    private final Cloudinary cloudinary;
    private CentreCampingRepository centreCampingRepository;

    @Override
    public List<CentreCamping> retrieveAllCentreCamping() {
        return centreCampingRepository.findAll();
    }

    @Override
    public CentreCamping addCentreCamping(CentreCamping centreCamping) {
        return centreCampingRepository.save(centreCamping);
    }

    @Override
    public CentreCamping updateCentreCamping(Long id, CentreCamping centreCamping) {
        CentreCamping existingCentreCamping = centreCampingRepository.findById(id).orElse(null);
        if (existingCentreCamping != null) {
            existingCentreCamping.setLongitude(centreCamping.getLongitude());
            existingCentreCamping.setLatitude(centreCamping.getLatitude());
            existingCentreCamping.setName(centreCamping.getName());
            existingCentreCamping.setCapcite(centreCamping.getCapcite());
            existingCentreCamping.setImage(centreCamping.getImage());
            // Update other fields as necessary
            return centreCampingRepository.save(existingCentreCamping);
        } else {
            return null;
        }
    }

    @Override
    public CentreCamping retrieveCentreCamping(Long idCentre) {
        return centreCampingRepository.findById(idCentre).orElse(null);
    }

    @Override
    public void removeCentreCamping(Long idCentre) {
        centreCampingRepository.deleteById(idCentre);
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
    public List<CentreCamping> retrieveVerifiedCentreCamping() {
        return centreCampingRepository.findVerifiedCentreCamping();
    }

    @Override
    public List<CentreCamping> retrieveCentreCampingByOwner(Long idOwner) {
        return centreCampingRepository.findByIdOwner(idOwner);
    }

    @Override
    public CentreCamping verifyCentreCamping(Long idCentre) {
        CentreCamping centreCamping = centreCampingRepository.findById(idCentre).orElse(null);
        if (centreCamping != null) {
            centreCamping.setVerified(true);
            return centreCampingRepository.save(centreCamping);
        } else {
            return null;
        }
    }

    @Override
    public CentreCamping deactivateCentreCamping(Long idCentre) {
        CentreCamping centreCamping = centreCampingRepository.findById(idCentre).orElse(null);
        if (centreCamping != null) {
            centreCamping.setVerified(false);
            return centreCampingRepository.save(centreCamping);
        } else {
            return null;
        }
    }

}
