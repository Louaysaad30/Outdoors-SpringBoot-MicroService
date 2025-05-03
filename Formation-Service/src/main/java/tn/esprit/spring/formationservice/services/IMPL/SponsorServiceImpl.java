package tn.esprit.spring.formationservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.entity.Sponsor;
import tn.esprit.spring.formationservice.repository.SponsorRepository;
import tn.esprit.spring.formationservice.services.interfaces.ICloudinaryService;
import tn.esprit.spring.formationservice.services.interfaces.IHuggingFaceService;
import tn.esprit.spring.formationservice.services.interfaces.ISponsorService;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SponsorServiceImpl implements ISponsorService {

    private final SponsorRepository sponsorRepository;
    private final ICloudinaryService cloudinaryService;
    private final IHuggingFaceService huggingFaceService;

    @Override
    public Sponsor addSponsor(Sponsor sponsor, MultipartFile logo) throws IOException {
        boolean exists = sponsorRepository.existsByNomOrContactEmail(sponsor.getNom(), sponsor.getContactEmail());
        if (exists) {
            throw new IllegalArgumentException("A sponsor with the same name or email already exists.");
        }

        if (logo != null && !logo.isEmpty()) {
            String logoUrl = cloudinaryService.uploadImage(logo);
            sponsor.setLogoUrl(logoUrl);
        }
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

        // Vérifie duplication du nom (hors de lui-même)
        if (sponsorRepository.existsByNomAndIdNot(sponsor.getNom(), id)) {
            throw new IllegalArgumentException("A sponsor with this name already exists.");
        }

        // Vérifie duplication de l'email (hors de lui-même)
        if (sponsorRepository.existsByContactEmailAndIdNot(sponsor.getContactEmail(), id)) {
            throw new IllegalArgumentException("A sponsor with this email already exists.");
        }

        if (logo != null && !logo.isEmpty()) {
            String logoUrl = cloudinaryService.uploadImage(logo);
            existing.setLogoUrl(logoUrl);
        }

        existing.setNom(sponsor.getNom());
        existing.setContactEmail(sponsor.getContactEmail());
        existing.setTelephone(sponsor.getTelephone());
        existing.setTypeSponsor(sponsor.getTypeSponsor());
        existing.setNiveauSponsor(sponsor.getNiveauSponsor());
        existing.setPays(sponsor.getPays());
        existing.setAdresse(sponsor.getAdresse());

        return sponsorRepository.save(existing);
    }

@Override
    public Optional<Sponsor> suggestBestSponsor(String desc) {
        List<Sponsor> sponsors = sponsorRepository.findAll();
        List<String> descriptions = sponsors.stream()
                .map(s -> s.getNom() + " " + s.getTypeSponsor() + " " + s.getNiveauSponsor())
                .toList();

        float[] scores = huggingFaceService.computeSimilarity(desc, descriptions);

        if (scores.length != sponsors.size()) return Optional.empty();

        int bestIdx = IntStream.range(0, scores.length)
                .boxed()
                .max(Comparator.comparingDouble(i -> scores[i]))
                .orElse(-1);

        return bestIdx != -1 ? Optional.of(sponsors.get(bestIdx)) : Optional.empty();
    }

}