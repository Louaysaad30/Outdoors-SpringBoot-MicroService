package tn.esprit.spring.formationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.formationservice.entity.Categorie;
import tn.esprit.spring.formationservice.services.interfaces.ICategorieService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategorieController {

    private final ICategorieService categorieService;

    @PostMapping
    public ResponseEntity<Categorie> add(@RequestBody Categorie categorie) {
        return new ResponseEntity<>(categorieService.addCategorie(categorie), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Categorie>> getAll() {
        return new ResponseEntity<>(categorieService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getById(@PathVariable Long id) {
        return new ResponseEntity<>(categorieService.getCategorieById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
