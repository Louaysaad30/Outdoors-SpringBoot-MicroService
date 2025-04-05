package tn.esprit.spring.marketplaceservice.services.IMPL;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.DTO.UpdateQuantiteDTO;
import tn.esprit.spring.marketplaceservice.DTO.UpdateTotalDTO;
import tn.esprit.spring.marketplaceservice.entity.LigneCommande;
import tn.esprit.spring.marketplaceservice.entity.Panier;
import tn.esprit.spring.marketplaceservice.repository.LigneCommandeRepository;
import tn.esprit.spring.marketplaceservice.repository.PanierRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.ILigneCommandeService;

import java.util.List;

@AllArgsConstructor
@Service
public class LigneCommandeServiceIMPL implements ILigneCommandeService {

    LigneCommandeRepository ligneCommandeRepository;
    PanierRepository panierRepository;

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
        return List.of();
    }

    @Override
    public List<LigneCommande> findByPanierId(Long panierId) {
        return ligneCommandeRepository.findAll().stream()
                .filter(ligneCommande -> ligneCommande.getPanier() != null && ligneCommande.getPanier().getId().equals(panierId))
                .toList();
    }

    @Override
    @Transactional
    public LigneCommande updateQuantiteAndTotal(Long idLigneCommande, UpdateQuantiteDTO dto) {
        // Find and update LigneCommande
        LigneCommande ligneCommande = ligneCommandeRepository.findById(idLigneCommande)
                .orElseThrow(() -> new RuntimeException("LigneCommande not found"));

        ligneCommande.setQuantite((long) dto.getQuantite());

        // Find and update Panier
        Panier panier = panierRepository.findById(dto.getIdPanier())
                .orElseThrow(() -> new RuntimeException("Panier not found"));
        panier.setTotal(dto.getTotal());
        panierRepository.save(panier);

        return ligneCommandeRepository.save(ligneCommande);
    }

    @Override
    public LigneCommande updateLigneCommande(LigneCommande ligneCommande) {
        return ligneCommandeRepository.save(ligneCommande);
    }
}