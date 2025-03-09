package tn.esprit.spring.campingservice.Services.IMPL;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.campingservice.Entity.CentreCamping;
import tn.esprit.spring.campingservice.Repository.CentreCampingRepository;
import tn.esprit.spring.campingservice.Services.Interfaces.ICentreCampingService;

import java.util.List;

@Service
@AllArgsConstructor
public class CentreCampingServiceIMPL implements ICentreCampingService {


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
    public CentreCamping updateCentreCamping(CentreCamping centreCamping) {
        return centreCampingRepository.save(centreCamping);
    }

    @Override
    public CentreCamping retrieveCentreCamping(Long idCentre) {
        return centreCampingRepository.findById(idCentre).orElse(null);
    }

    @Override
    public void removeCentreCamping(Long idCentre) {
        centreCampingRepository.deleteById(idCentre);
    }
}
