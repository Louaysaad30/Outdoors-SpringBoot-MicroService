package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.entity.Commande;

import java.util.List;

public interface ICommandeService {
    List<Commande> retrieveCommandes();
    Commande addCommande(Commande commande);
    Commande updateCommande(Commande commande);
    Commande retrieveCommande(long idCommande);
    void removeCommande(long idCommande);
}
