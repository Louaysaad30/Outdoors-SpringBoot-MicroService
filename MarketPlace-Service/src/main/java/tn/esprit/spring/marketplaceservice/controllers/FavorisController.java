package tn.esprit.spring.marketplaceservice.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.entity.Favoris;
import tn.esprit.spring.marketplaceservice.services.interfaces.IFavorisService;

import java.util.List;

@Tag(name = "Gestion Favoris")
@RestController
@AllArgsConstructor
@RequestMapping("/Favoris")
public class FavorisController {

    private final IFavorisService iFavorisService;

    @GetMapping("/getAllFavoris")
    public List<Favoris> retrieveAllFavoris() {
        return iFavorisService.retrieveAllFavoris();
    }

    @PostMapping("/addFavoris")
    public Favoris addFavoris(@RequestBody Favoris favoris) {
        return iFavorisService.addFavoris(favoris);
    }

    @PutMapping("/update")
    public Favoris updateFavoris(Favoris favoris) {
        return iFavorisService.updateFavoris(favoris);
    }

    @GetMapping("/get/{idFavoris}")
    public Favoris retrieveFavoris(@PathVariable long idFavoris) {
        return iFavorisService.retrieveFavoris(idFavoris);
    }

    @DeleteMapping("/delete/{idFavoris}")
    public void removeFavoris(@PathVariable long idFavoris) {
        iFavorisService.removeFavoris(idFavoris);
    }

    @GetMapping("/getByUserId/{userId}")
    public List<Favoris> retrieveFavorisByUserId(@PathVariable Long userId) {
        return iFavorisService.retrieveFavorisByUserId(userId);
    }
}
