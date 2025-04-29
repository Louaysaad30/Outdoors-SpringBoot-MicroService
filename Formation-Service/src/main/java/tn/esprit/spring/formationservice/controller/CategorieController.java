package tn.esprit.spring.formationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.entity.Categorie;
import tn.esprit.spring.formationservice.services.interfaces.ICategorieService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final ICategorieService categorieService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Categorie> add(
            @RequestPart("categorie") Categorie categorie,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {
        return new ResponseEntity<>(categorieService.addCategorie(categorie, imageFile), HttpStatus.CREATED);
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