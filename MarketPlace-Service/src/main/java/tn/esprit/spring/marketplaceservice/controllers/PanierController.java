package tn.esprit.spring.marketplaceservice.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.entity.Panier;
import tn.esprit.spring.marketplaceservice.services.interfaces.IPanierService;

import java.util.List;

@Tag(name = "Gestion Panier")
@RestController
@AllArgsConstructor
@RequestMapping("/Panier")
public class PanierController {

    private final IPanierService iPanierService;

    @GetMapping("/getAllPaniers")
    public List<Panier> retrievePaniers() {
        return iPanierService.retrievePaniers();
    }

    @PostMapping("/addPanier")
    public Panier addPanier(Panier panier) {
        return iPanierService.addPanier(panier);
    }

    @PutMapping("/update")
    public Panier updatePanier(Panier panier) {
        return iPanierService.updatePanier(panier);
    }

    @GetMapping("/get/{idPanier}")
    public Panier retrievePanier(@PathVariable long idPanier) {
        return iPanierService.retrievePanier(idPanier);
    }

    @DeleteMapping("/delete/{idPanier}")
    public void removePanier(@PathVariable long idPanier) {
        iPanierService.removePanier(idPanier);
    }

    @PutMapping("/ajouterProduitAuPanier/{userId}/{produitId}/{quantite}")
    public Panier ajouterProduitAuPanier(@PathVariable Long userId, @PathVariable Long produitId, @PathVariable Long quantite) {
        return iPanierService.ajouterProduitAuPanier(userId, produitId, quantite);
    }

    @GetMapping("/getPanierByUser/{idUser}")
    public Panier getPanierByUser(@PathVariable Long idUser) {
        return iPanierService.getPanierByUser(idUser);
    }

}
