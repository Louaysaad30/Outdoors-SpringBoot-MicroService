package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.entity.Panier;

import java.util.List;

public interface IPanierService {
    List<Panier> retrievePaniers();
    Panier addPanier(Panier panier);
    Panier updatePanier(Panier panier);
    Panier retrievePanier(long idPanier);
    void removePanier(long idPanier);
    Panier ajouterProduitAuPanier(Long userId, Long produitId, Long quantite);
    Panier getPanierByUser(Long idUser);
    Panier updateTotal(Long panierId, Double newTotal);
    List<Panier> getAllPaniersByUserId(Long userId);
    Panier validatePanier(Long panierId);

}
