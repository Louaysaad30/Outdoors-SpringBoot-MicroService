package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.DTO.UpdateQuantiteDTO;
import tn.esprit.spring.marketplaceservice.DTO.UpdateTotalDTO;
import tn.esprit.spring.marketplaceservice.entity.Commande;
import tn.esprit.spring.marketplaceservice.entity.LigneCommande;

import java.util.List;

public interface ILigneCommandeService {
    List<LigneCommande> retrieveLigneCommandes();
    LigneCommande addLigneCommande(LigneCommande ligneCommande);
    LigneCommande updateLigneCommande(LigneCommande ligneCommande);
    LigneCommande retrieveLigneCommande(long idLigneCommande);
    void removeLigneCommande(long idLigneCommande);
    List<LigneCommande> getLigneCommandesByPanierId(Long panierId);
    List<LigneCommande> findByPanierId(Long panierId);
    LigneCommande updateQuantiteAndTotal(Long idLigneCommande, UpdateQuantiteDTO dto);
    LigneCommande affecterCommandeToLigneCommande(Long idLigneCommande,Long idCommande);
    List<LigneCommande> findByCommandeId(Long idCommande);




}
