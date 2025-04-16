package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.entity.Produit;

import java.util.List;

public interface IProduitService {
    List<Produit> retrieveProduits();
    Produit addProduit(Produit produit);
    Produit updateProduit(Produit produit);
    Produit retrieveProduit(long idProduit);
    void removeProduit(long idProduit);
    Produit affecterProduitCategorie(long idProduit, long idCategorie);
    Produit desaffecterProduitCategorie(long idProduit);
    Produit affecterProduitCode(long idProduit, long idCode);
    Produit desaffecterProduitCode(long idProduit);
    List<Produit> getProduitsByCode(Long code);
}
