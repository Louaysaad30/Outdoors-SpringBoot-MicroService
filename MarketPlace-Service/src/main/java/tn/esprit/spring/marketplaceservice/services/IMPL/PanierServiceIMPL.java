package tn.esprit.spring.marketplaceservice.services.IMPL;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.entity.LigneCommande;
import tn.esprit.spring.marketplaceservice.entity.Panier;
import tn.esprit.spring.marketplaceservice.entity.Produit;
import tn.esprit.spring.marketplaceservice.repository.LigneCommandeRepository;
import tn.esprit.spring.marketplaceservice.repository.PanierRepository;
import tn.esprit.spring.marketplaceservice.repository.ProduitRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.IPanierService;

import java.util.List;

@AllArgsConstructor
@Service
public class PanierServiceIMPL implements IPanierService {
    PanierRepository panierRepository;
    LigneCommandeRepository ligneCommandeRepository;
    ProduitRepository produitRepository;
    @Override
    public List<Panier> retrievePaniers() {
        return panierRepository.findAll();
    }

    @Override
    public Panier updatePanier(Panier panier) {
        return panierRepository.save(panier);
    }

    @Override
    public Panier retrievePanier(long idPanier) {
        return panierRepository.findById(idPanier).orElse(null);
    }

    @Override
    public void removePanier(long idPanier) {
        panierRepository.deleteById(idPanier);
    }

    @Override
    public Panier addPanier(Panier panier) {
        return panierRepository.save(panier);

    }

    private Double calculerTotalPanier(Panier panier) {
        return panier.getLignesCommande().stream()
                .mapToDouble(lc -> lc.getPrix())
                .sum();
    }

    @Override
    public Panier ajouterProduitAuPanier(Long userId, Long produitId, Long quantite) {
        Panier panier = panierRepository.findByUserId(userId);

        if (panier == null) {
            panier = new Panier();
            panier.setUserId(userId);
            panier.setTotal(0.0);
            panier = panierRepository.save(panier);
        }

        Produit produit = produitRepository.findById(produitId).orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Vérifier si le produit existe déjà dans la ligne de commande
        LigneCommande ligneCommande = ligneCommandeRepository.findByPanierAndProduit(panier, produit);

        if (ligneCommande != null) {
            // Si le produit existe, incrémenter la quantité et mettre à jour le prix
            ligneCommande.setQuantite(ligneCommande.getQuantite() + quantite);
            ligneCommande.setPrix(produit.getPrixProduit() * ligneCommande.getQuantite());
            ligneCommandeRepository.save(ligneCommande);
        } else {
            // Si le produit n'existe pas, créer une nouvelle ligne de commande
            ligneCommande = new LigneCommande();
            ligneCommande.setPanier(panier);
            ligneCommande.setProduit(produit);
            ligneCommande.setQuantite(quantite);
            ligneCommande.setPrix(produit.getPrixProduit() * quantite);
            ligneCommandeRepository.save(ligneCommande);
        }

        // Mettre à jour le total du panier
        panier.setTotal(panier.getTotal() + ligneCommande.getPrix());
        panierRepository.save(panier);

        return panier;
    }
    @Override
    public Panier getPanierByUser(Long idUser) {
        return panierRepository.findByUserId(idUser);
    }

    @Override
    @Transactional
    public Panier updateTotal(Long panierId, Double newTotal) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new RuntimeException("Panier not found with id: " + panierId));

        panier.setTotal(newTotal);
        return panierRepository.save(panier);
    }

    @Override
    public List<Panier> getAllPaniersByUserId(Long userId) {
        return panierRepository.findAllByUserId(userId);
    }

    @Override
    public Panier validatePanier(Long panierId) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new RuntimeException("Panier not found with id: " + panierId));
            panier.setValidated(true);
            return panierRepository.save(panier);

    }


}
