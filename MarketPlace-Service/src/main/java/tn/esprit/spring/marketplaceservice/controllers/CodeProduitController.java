package tn.esprit.spring.marketplaceservice.controllers;




import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.entity.CodeProduit;
import tn.esprit.spring.marketplaceservice.entity.Produit;
import tn.esprit.spring.marketplaceservice.services.interfaces.ICodeProduitService;
import tn.esprit.spring.marketplaceservice.services.interfaces.IProduitService;

import java.util.List;

@Tag(name = "Gestion CodeProduit")
@RestController
@AllArgsConstructor
@RequestMapping("/CodeProduit")
@CrossOrigin(origins = "http://localhost:4200")
public class CodeProduitController {
    private final ICodeProduitService iCodeProduitService;

    @PostMapping("/add")
    public CodeProduit addCodeProduit(@RequestBody CodeProduit codeProduit) {
        return iCodeProduitService.addCodeProduit(codeProduit);
    }

    @GetMapping("/get/{code}")
    public CodeProduit retrieveCodeProduit(@PathVariable String code) {
        return iCodeProduitService.retrieveCodeProduit(code);
    }

    @DeleteMapping("/delete/{idCodeProduit}")
    public void deleteCodeProduit(@PathVariable Long idCodeProduit) {
        iCodeProduitService.deleteCodeProduit(idCodeProduit);
    }

    @PutMapping("/update")
    public CodeProduit updateCodeProduit(@RequestBody CodeProduit codeProduit) {
        return iCodeProduitService.updateCodeProduit(codeProduit);
    }

    @GetMapping("/getAllCodeProduits")
    public List<CodeProduit> retrieveCodeProduits() {
        return iCodeProduitService.retrieveCodeProduits();
    }


}
