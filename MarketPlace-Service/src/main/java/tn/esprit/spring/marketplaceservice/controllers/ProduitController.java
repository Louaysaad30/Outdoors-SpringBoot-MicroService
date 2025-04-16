package tn.esprit.spring.marketplaceservice.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<Produit> updateProduit(@PathVariable("id") Long id, @RequestBody Produit produit) {
        try {
            Produit existingProduit = iProduitService.retrieveProduit(id);
            if (existingProduit == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Update the existing product with new values
            existingProduit.setNomProduit(produit.getNomProduit());
            existingProduit.setDescriptionProduit(produit.getDescriptionProduit());
            existingProduit.setPrixProduit(produit.getPrixProduit());
            existingProduit.setStockProduit(produit.getStockProduit());
            existingProduit.setCategorie(produit.getCategorie());
            existingProduit.setImageProduit(produit.getImageProduit());

            Produit updatedProduit = iProduitService.updateProduit(existingProduit);
            return new ResponseEntity<>(updatedProduit, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @PutMapping("/affecterProduitCodeProduit/{idProduit}/{idCodeProduit}")
    public Produit affecterProduitCodeProduit(@PathVariable long idProduit, @PathVariable long idCodeProduit) {
        return iProduitService.affecterProduitCode(idProduit, idCodeProduit);
    }

    @PutMapping("/desaffecterProduitCodeProduit/{idProduit}")
    public Produit desaffecterProduitCodeProduit(@PathVariable long idProduit) {
        return iProduitService.desaffecterProduitCode(idProduit);
    }

    @GetMapping("/getByCode/{codeId}")
    public List<Produit> getProductsByCode(@PathVariable long codeId) {
        return iProduitService.getProduitsByCode(codeId);
    }


}
