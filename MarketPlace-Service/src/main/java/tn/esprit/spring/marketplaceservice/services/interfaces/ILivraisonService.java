package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.entity.Livraison;

import java.util.List;

public interface ILivraisonService {
    List<Livraison> retrieveLivraisons();
    Livraison addLivraison(Livraison livraison);
    Livraison updateLivraison(Livraison livraison);
    Livraison retrieveLivraison(long idLivraison);
    void removeLivraison(long idLivraison);
}
