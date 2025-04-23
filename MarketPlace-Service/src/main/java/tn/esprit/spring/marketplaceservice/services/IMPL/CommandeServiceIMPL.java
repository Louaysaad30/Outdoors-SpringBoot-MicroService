package tn.esprit.spring.marketplaceservice.services.IMPL;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.entity.Commande;
import tn.esprit.spring.marketplaceservice.entity.LigneCommande;
import tn.esprit.spring.marketplaceservice.entity.Panier;
import tn.esprit.spring.marketplaceservice.entity.Status;
import tn.esprit.spring.marketplaceservice.repository.CommandeRepository;
import tn.esprit.spring.marketplaceservice.repository.LigneCommandeRepository;
import tn.esprit.spring.marketplaceservice.repository.PanierRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.ICommandeService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CommandeServiceIMPL implements ICommandeService {

    CommandeRepository commandeRepository;
    LigneCommandeRepository ligneCommandeRepository;
    @Override
    public List<Commande> retrieveCommandes() {
        return commandeRepository.findAll();
    }

    @Override
    public Commande updateCommande(Commande commande) {
        return commandeRepository.save(commande);
    }

    @Override
    public Commande addCommande(Commande commande) {
        return commandeRepository.save(commande);
    }

    @Override
    public Commande retrieveCommande(long idCommande) {
        return commandeRepository.findById(idCommande).orElse(null);
    }

    @Override
    public void removeCommande(long idCommande) {
         commandeRepository.deleteById(idCommande);
    }

    @Override
    public List<Commande> findByUserIdAndEtat(Long userId, Status etat) {
        return commandeRepository.findByUserIdAndEtat(userId, etat);
    }

    @Override
    public byte[] generateInvoice(Long orderId) {
        // Mock implementation for generating a PDF
        // Replace this with actual PDF generation logic
        String invoiceContent = "Invoice for Order ID: " + orderId;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Use a library like iText or Apache PDFBox to generate a real PDF
            outputStream.write(invoiceContent.getBytes());
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating invoice", e);
        }
    }

    @Override
    public List<String> getProductNamesByCommandeId(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + commandeId));

        return commande.getLigneCommande().stream()
                .map(ligneCommande -> ligneCommande.getProduit().getNomProduit())
                .collect(Collectors.toList());
    }


}
