package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.DTO.UpdateIdlivreurDTO;
import tn.esprit.spring.marketplaceservice.DTO.UpdateQuantiteDTO;
import tn.esprit.spring.marketplaceservice.DTO.UpdateStateCommand;
import tn.esprit.spring.marketplaceservice.entity.Commande;
import tn.esprit.spring.marketplaceservice.entity.LigneCommande;
import tn.esprit.spring.marketplaceservice.entity.Livraison;
import tn.esprit.spring.marketplaceservice.entity.Status;

import java.util.List;

public interface ICommandeService {
    List<Commande> retrieveCommandes();
    Commande addCommande(Commande commande);
    Commande updateCommande(Commande commande);
    Commande retrieveCommande(long idCommande);
    void removeCommande(long idCommande);
    List<Commande> findByUserIdAndEtat(Long userId, Status etat);
    byte[] generateInvoice(Long orderId);
    List<String> getProductNamesByCommandeId(Long commandeId);
    Commande updateCommandeStatus(Long commandeId,  UpdateStateCommand dto);
    List<Commande> findByUserId(Long userId);
    Commande affecterLivreurACommande(Long commandeId, Long livraisonId);
    List<Commande> findByLivraisonIdLivraison(Long idLivraison);

}
