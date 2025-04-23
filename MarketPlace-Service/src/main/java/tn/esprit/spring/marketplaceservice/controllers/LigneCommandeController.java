package tn.esprit.spring.marketplaceservice.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.DTO.UpdateQuantiteDTO;
import tn.esprit.spring.marketplaceservice.DTO.UpdateTotalDTO;
import tn.esprit.spring.marketplaceservice.entity.LigneCommande;
import tn.esprit.spring.marketplaceservice.services.interfaces.ILigneCommandeService;

import java.util.List;

@Tag(name = "Gestion LigneCommande")
@RestController
@AllArgsConstructor
@RequestMapping("/LigneCommande")
public class LigneCommandeController {
    private final ILigneCommandeService iLigneCommandeService;

    @GetMapping("/getAllLigneCommandes")
    public List<LigneCommande> retrieveLigneCommandes() {
        return iLigneCommandeService.retrieveLigneCommandes();
    }

    @PostMapping("/addLigneCommande")
    public LigneCommande addLigneCommande(@RequestBody LigneCommande ligneCommande) {
        return iLigneCommandeService.addLigneCommande(ligneCommande);
    }

    @PutMapping("/update")
    public LigneCommande updateLigneCommande(LigneCommande ligneCommande) {
        return iLigneCommandeService.updateLigneCommande(ligneCommande);
    }

    @GetMapping("/get/{idLigneCommande}")
    public LigneCommande retrieveLigneCommande(@PathVariable long idLigneCommande) {
        return iLigneCommandeService.retrieveLigneCommande(idLigneCommande);
    }

    @DeleteMapping("/delete/{idLigneCommande}")
    public void removeLigneCommande(@PathVariable long idLigneCommande) {
        iLigneCommandeService.removeLigneCommande(idLigneCommande);
    }

    // In your LigneCommandeController
    @GetMapping("/getByPanier/{panierId}")
    public List<LigneCommande> getLigneCommandesByPanierId(@PathVariable Long panierId) {
        List<LigneCommande> lignes = iLigneCommandeService.findByPanierId(panierId);
        System.out.println("Sending lignes: " + lignes); // Debug line
        return lignes;
    }

    @PutMapping("/updateQuantite/{id}")
    public ResponseEntity<LigneCommande> updateQuantite(@PathVariable("id") Long id, @RequestBody UpdateQuantiteDTO dto) {
        try {
            LigneCommande updated = iLigneCommandeService.updateQuantiteAndTotal(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/affecterCommandeToLigneCommande/{idLigneCommande}/{idCommande}")
    public LigneCommande affecterCommandeToLigneCommande(@PathVariable Long idLigneCommande, @PathVariable Long idCommande) {
        return iLigneCommandeService.affecterCommandeToLigneCommande(idLigneCommande, idCommande);
    }

    @GetMapping("/getByCommande/{idCommande}")
    public List<LigneCommande> getByCommandeId(@PathVariable Long idCommande) {
        return iLigneCommandeService.findByCommandeId(idCommande);
    }

    // LigneCommandeController.java

}
