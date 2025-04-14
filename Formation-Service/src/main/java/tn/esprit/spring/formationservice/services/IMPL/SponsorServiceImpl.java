package tn.esprit.spring.formationservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.entity.Sponsor;
import tn.esprit.spring.formationservice.repository.SponsorRepository;
import tn.esprit.spring.formationservice.services.interfaces.ICloudinaryService;
import tn.esprit.spring.formationservice.services.interfaces.ISponsorService;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorServiceImpl implements ISponsorService {

    private final SponsorRepository sponsorRepository;
    private final ICloudinaryService cloudinaryService;

    @Override
    public Sponsor addSponsor(Sponsor sponsor, MultipartFile logo) throws IOException {
        String logoUrl = cloudinaryService.uploadImage(logo);
        sponsor.setLogoUrl(logoUrl);
        return sponsorRepository.save(sponsor);
    }

    @Override
    public List<Sponsor> getAllSponsors() {
        return sponsorRepository.findAll();
    }

    @Override
    public void deleteSponsor(Long id) {
        sponsorRepository.deleteById(id);
    }

    @Override
    public Sponsor updateSponsor(Long id, Sponsor sponsor, MultipartFile logo) throws IOException {
        Sponsor existing = sponsorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        if (logo != null && !logo.isEmpty()) {
            String logoUrl = cloudinaryService.uploadImage(logo);
            existing.setLogoUrl(logoUrl);
        }
        existing.setNom(sponsor.getNom());
        existing.setContactEmail(sponsor.getContactEmail());
        return sponsorRepository.save(existing);
    }
}