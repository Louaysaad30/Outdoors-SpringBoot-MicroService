package tn.esprit.spring.marketplaceservice.services.IMPL;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.entity.Commande;
import tn.esprit.spring.marketplaceservice.repository.CommandeRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.ICommandeService;

import java.util.List;

@AllArgsConstructor
@Service
public class CommandeServiceIMPL implements ICommandeService {

    CommandeRepository commandeRepository;
    @Override
    public List<Commande> retrieveCommandes() {
        return commandeRepository.findAll();
    }

    @Override
    public Commande updateCommande(Commande commande) {
        return commandeRepository.save(commande);
    }

    @Override
    public Commande addCommande(Commande commande) {
        return commandeRepository.save(commande);
    }

    @Override
    public Commande retrieveCommande(long idCommande) {
        return commandeRepository.findById(idCommande).orElse(null);
    }

    @Override
    public void removeCommande(long idCommande) {
         commandeRepository.deleteById(idCommande);
    }
}
