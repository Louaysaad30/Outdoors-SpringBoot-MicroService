package tn.esprit.spring.marketplaceservice.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.entity.Panier;
import tn.esprit.spring.marketplaceservice.services.interfaces.IPanierService;

import java.util.List;
import java.util.Map;

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
    public Panier addPanier(@RequestBody Panier panier) {
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

    @PutMapping("/updateTotal/{id}")
    public ResponseEntity<Panier> updateTotal(@PathVariable("id") Long id, @RequestBody Map<String, Double> payload) {
        try {
            Double newTotal = payload.get("total");
            if (newTotal == null) {
                return ResponseEntity.badRequest().build();
            }

            Panier updatedPanier = iPanierService.updateTotal(id, newTotal);
            return ResponseEntity.ok(updatedPanier);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/getAllPaniersByUserId/{userId}")
    public List<Panier> getAllPaniersByUserId(@PathVariable Long userId) {
        return iPanierService.getAllPaniersByUserId(userId);
    }

    @PutMapping("/validatePanier/{panierId}")
    public ResponseEntity<Panier> validatePanier(@PathVariable Long panierId) {
        try {
            Panier validatedPanier = iPanierService.validatePanier(panierId);
            return ResponseEntity.ok(validatedPanier);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
