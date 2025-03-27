package tn.esprit.spring.marketplaceservice.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.entity.Commande;
import tn.esprit.spring.marketplaceservice.services.interfaces.ICommandeService;

import java.util.List;

@Tag(name = "Gestion Commande")
@RestController
@AllArgsConstructor
@RequestMapping("/Commande")
public class CommandeController {

    private final ICommandeService iCommandeService;

    @GetMapping("/getAllCommandes")
    public List<Commande> retrieveCommandes() {
        return iCommandeService.retrieveCommandes();
    }

    @PostMapping("/addCommande")
    public Commande addCommande(Commande commande) {
        return iCommandeService.addCommande(commande);
    }

    @PutMapping("/update")
    public Commande updateCommande(Commande commande) {
        return iCommandeService.updateCommande(commande);
    }

    @GetMapping("/get/{idCommande}")
    public Commande retrieveCommande(@PathVariable long idCommande) {
        return iCommandeService.retrieveCommande(idCommande);
    }

    @DeleteMapping("/delete/{idCommande}")
    public void removeCommande(@PathVariable long idCommande) {
        iCommandeService.removeCommande(idCommande);
    }
}
