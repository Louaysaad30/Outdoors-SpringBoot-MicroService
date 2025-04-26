package tn.esprit.spring.formationservice.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.entity.Sponsor;

import java.io.IOException;
import java.util.List;

public interface ISponsorService {
    Sponsor addSponsor(Sponsor sponsor, MultipartFile logo) throws IOException;
    List<Sponsor> getAllSponsors();
    void deleteSponsor(Long id);
    Sponsor updateSponsor(Long id, Sponsor sponsor, MultipartFile logo) throws IOException;
}