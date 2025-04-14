package tn.esprit.spring.marketplaceservice.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.entity.Livraison;
import tn.esprit.spring.marketplaceservice.services.interfaces.ILivraisonService;

import java.util.List;

@Tag(name = "Gestion Livraison")
@RestController
@AllArgsConstructor
@RequestMapping("/Livraison")
public class LivraisonController {
    private final ILivraisonService iLivraisonService;

    @GetMapping("/getAllLivraisons")
    public List<Livraison> retrieveLivraisons() {
        return iLivraisonService.retrieveLivraisons();
    }

    @PostMapping("/addLivraison")
    public Livraison addLivraison(Livraison livraison) {
        return iLivraisonService.addLivraison(livraison);
    }

    @PutMapping("/update")
    public Livraison updateLivraison(Livraison livraison) {
        return iLivraisonService.updateLivraison(livraison);
    }

    @GetMapping("/get/{idLivraison}")
    public Livraison retrieveLivraison(@PathVariable long idLivraison) {
        return iLivraisonService.retrieveLivraison(idLivraison);
    }

    @DeleteMapping("/delete/{idLivraison}")
    public void removeLivraison(@PathVariable long idLivraison) {
        iLivraisonService.removeLivraison(idLivraison);
    }
}
