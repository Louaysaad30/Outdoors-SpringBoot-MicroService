package tn.esprit.spring.marketplaceservice.services.IMPL;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.DTO.UpdateQuantiteDTO;
import tn.esprit.spring.marketplaceservice.DTO.UpdateTotalDTO;
import tn.esprit.spring.marketplaceservice.entity.Commande;
import tn.esprit.spring.marketplaceservice.entity.LigneCommande;
import tn.esprit.spring.marketplaceservice.entity.Panier;
import tn.esprit.spring.marketplaceservice.repository.CommandeRepository;
import tn.esprit.spring.marketplaceservice.repository.LigneCommandeRepository;
import tn.esprit.spring.marketplaceservice.repository.PanierRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.ILigneCommandeService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Service
public class LigneCommandeServiceIMPL implements ILigneCommandeService {

    LigneCommandeRepository ligneCommandeRepository;
    PanierRepository panierRepository;
    CommandeRepository commandeRepository;

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
    public LigneCommande affecterCommandeToLigneCommande(Long idLigneCommande, Long idCommande) {
        LigneCommande ligneCommande = ligneCommandeRepository.findById(idLigneCommande)
                .orElseThrow(() -> new RuntimeException("LigneCommande not found"));
        Commande commande = commandeRepository.findById(idCommande)
                .orElseThrow(() -> new RuntimeException("Commande not found"));

        ligneCommande.setCommande(commande);
        return ligneCommandeRepository.save(ligneCommande);
    }

    @Override
    public List<LigneCommande> findByCommandeId(Long idCommande) {
        return ligneCommandeRepository.findByCommandeIdCommande(idCommande);
    }

    @Override
    public LigneCommande updateLigneCommande(LigneCommande ligneCommande) {
        return ligneCommandeRepository.save(ligneCommande);
    }

    // LigneCommandeServiceIMPL.java



}