package tn.esprit.spring.marketplaceservice.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.entity.Commande;
import tn.esprit.spring.marketplaceservice.services.interfaces.ICommandeService;

import java.util.List;

@Tag(name = "Gestion Commande")
@RestController
@AllArgsConstructor
@RequestMapping("/Commande")
public class CommandeController {

    private final ICommandeService iCommandeService;

    @GetMapping("/getAllCommandes")
    public List<Commande> retrieveCommandes() {
        return iCommandeService.retrieveCommandes();
    }

    @PostMapping("/addCommande")
    public Commande addCommande(@RequestBody Commande commande) {
        return iCommandeService.addCommande(commande);
    }

    @PutMapping("/update")
    public Commande updateCommande(Commande commande) {
        return iCommandeService.updateCommande(commande);
    }

    @GetMapping("/get/{idCommande}")
    public Commande retrieveCommande(@PathVariable long idCommande) {
        return iCommandeService.retrieveCommande(idCommande);
    }

    @DeleteMapping("/delete/{idCommande}")
    public void removeCommande(@PathVariable long idCommande) {
        iCommandeService.removeCommande(idCommande);
    }

    @GetMapping("/getByUserIdAndStatus/{userId}/{etat}")
    public List<Commande> getByUserIdAndStatus(@PathVariable Long userId, @PathVariable String etat) {
        return iCommandeService.findByUserIdAndEtat(userId, etat);
    }

    @GetMapping("/invoice/{orderId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long orderId) {
        try {
            byte[] pdfContent = iCommandeService.generateInvoice(orderId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_" + orderId + ".pdf");
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
