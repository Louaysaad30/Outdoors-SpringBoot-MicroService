package tn.esprit.spring.marketplaceservice.services.IMPL;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.entity.LigneCommande;
import tn.esprit.spring.marketplaceservice.repository.LigneCommandeRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.ILigneCommandeService;

import java.util.List;

@AllArgsConstructor
@Service
public class LigneCommandeServiceIMPL implements ILigneCommandeService {

    LigneCommandeRepository ligneCommandeRepository;

    @Override
    public List<LigneCommande> retrieveLigneCommandes() {
        return ligneCommandeRepository.findAll();
    }

    @Override
    public LigneCommande addLigneCommande(LigneCommande ligneCommande) {
        return ligneCommandeRepository.save(ligneCommande);
    }

    @Override
    public LigneCommande retrieveLigneCommande(long idLigneCommande) {
        return ligneCommandeRepository.findById(idLigneCommande).orElse(null);
    }

    @Override
    public void removeLigneCommande(long idLigneCommande) {
        ligneCommandeRepository.deleteById(idLigneCommande);
    }

    @Override
    public List<LigneCommande> getLigneCommandesByPanierId(Long panierId) {
        return ligneCommandeRepository.findAll().stream()
                .filter(ligneCommande -> ligneCommande.getPanier().getId().equals(panierId))
                .toList();
    }

    @Override
    public LigneCommande updateLigneCommande(LigneCommande ligneCommande) {
        return ligneCommandeRepository.save(ligneCommande);
    }

    @Override
    public List<LigneCommande> findByPanierId(Long panierId) {
        return ligneCommandeRepository.findAll().stream()
                .filter(ligneCommande -> ligneCommande.getPanier().getId().equals(panierId))
                .toList();
    }
}
