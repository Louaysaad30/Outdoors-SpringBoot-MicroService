package tn.esprit.spring.campingservice.Services.Interfaces;

import tn.esprit.spring.campingservice.Entity.CentreCamping;

import java.util.List;

public interface ICentreCampingService {
    List<CentreCamping> retrieveAllCentreCamping();
    CentreCamping addCentreCamping(CentreCamping centreCamping);
    CentreCamping updateCentreCamping(CentreCamping centreCamping);
    CentreCamping retrieveCentreCamping(Long idCentre);
    void removeCentreCamping(Long idCentre);

}
