package tn.esprit.spring.marketplaceservice.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.entity.Produit;
import tn.esprit.spring.marketplaceservice.services.interfaces.IProduitService;

import java.util.List;

@Tag(name = "Gestion Produit")
@RestController
@AllArgsConstructor
@RequestMapping("/Produit")
@CrossOrigin(origins = "http://localhost:4200")
public class ProduitController {
    private final IProduitService iProduitService;

    @GetMapping("/getAllProduits")
    public List<Produit> retrieveProduits() {
        return iProduitService.retrieveProduits();
    }

   @PutMapping("/update")
    public Produit updateProduit(Produit produit) {
        return iProduitService.updateProduit(produit);
    }

    @GetMapping("/get/{idProduit}")
    public Produit retrieveProduit(@PathVariable long idProduit) {
        return iProduitService.retrieveProduit(idProduit);
    }

    @DeleteMapping("/delete/{idProduit}")
    public void removeProduit(@PathVariable long idProduit) {
        iProduitService.removeProduit(idProduit);
    }

    @PostMapping("/add")
    public Produit addProduit(@RequestBody Produit produit) {
        return iProduitService.addProduit(produit);
    }

    @PutMapping("/affecterProduitCategorie/{idProduit}/{idCategorie}")
    public Produit affecterProduitCategorie(@PathVariable long idProduit, @PathVariable long idCategorie) {
        return iProduitService.affecterProduitCategorie(idProduit, idCategorie);
    }

    @PutMapping("/desaffecterProduitCategorie/{idProduit}")
    public Produit desaffecterProduitCategorie(@PathVariable long idProduit) {
        return iProduitService.desaffecterProduitCategorie(idProduit);
    }
}
