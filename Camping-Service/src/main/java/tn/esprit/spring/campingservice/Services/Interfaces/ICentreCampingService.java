package tn.esprit.spring.campingservice.Services.Interfaces;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.campingservice.Entity.CentreCamping;
import tn.esprit.spring.campingservice.Entity.Logement;

import java.util.List;

public interface ICentreCampingService {
    List<CentreCamping> retrieveAllCentreCamping();
    CentreCamping addCentreCamping(CentreCamping centreCamping);
    CentreCamping updateCentreCamping(Long id, CentreCamping centreCamping);
    CentreCamping retrieveCentreCamping(Long idCentre);
    void removeCentreCamping(Long idCentre);
    String uploadFile(MultipartFile file);
    List<CentreCamping> retrieveVerifiedCentreCamping();
    List<CentreCamping> retrieveCentreCampingByOwner(Long idOwner);
    CentreCamping verifyCentreCamping(Long idCentre);
    CentreCamping deactivateCentreCamping(Long idCentre);


}
