package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.entity.LigneCommande;

import java.util.List;

public interface ILigneCommandeService {
    List<LigneCommande> retrieveLigneCommandes();
    LigneCommande addLigneCommande(LigneCommande ligneCommande);
    LigneCommande updateLigneCommande(LigneCommande ligneCommande);
    LigneCommande retrieveLigneCommande(long idLigneCommande);
    void removeLigneCommande(long idLigneCommande);
}
