package tn.esprit.spring.marketplaceservice.controllers;


import feign.Body;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.entity.PCategorie;
import tn.esprit.spring.marketplaceservice.services.interfaces.IPCategorieService;
import tn.esprit.spring.marketplaceservice.services.interfaces.IProduitService;

import java.util.List;

@Tag(name = "Gestion PCategorie")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/PCategorie")
public class PCategorieController {
    private final IPCategorieService ipCategorieService;

    @GetMapping("/getAllCategories")
    public List<PCategorie> retrieveCategories() {
        return ipCategorieService.retrieveCategories();
    }

    @PostMapping("/addCategorie")
    public PCategorie addCategorie(@RequestBody PCategorie categorie) {
        return ipCategorieService.addCategorie(categorie);
    }

    @PutMapping("/update")
    public PCategorie updateCategorie(@RequestBody PCategorie categorie) {
        return ipCategorieService.updateCategorie(categorie);
    }

    @GetMapping("/get/{idCategorie}")
    public PCategorie retrieveCategorie(@PathVariable long idCategorie) {
        return ipCategorieService.retrieveCategorie(idCategorie);
    }

    @DeleteMapping("/delete/{idCategorie}")
    public void removeCategorie(@PathVariable long idCategorie) {
        ipCategorieService.removeCategorie(idCategorie);
    }
}
