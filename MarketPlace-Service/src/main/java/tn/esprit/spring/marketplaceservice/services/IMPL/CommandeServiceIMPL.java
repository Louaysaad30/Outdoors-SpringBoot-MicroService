package tn.esprit.spring.marketplaceservice.services.IMPL;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.DTO.UpdateIdlivreurDTO;
import tn.esprit.spring.marketplaceservice.DTO.UpdateQuantiteDTO;
import tn.esprit.spring.marketplaceservice.DTO.UpdateStateCommand;
import tn.esprit.spring.marketplaceservice.entity.*;
import tn.esprit.spring.marketplaceservice.repository.CommandeRepository;
import tn.esprit.spring.marketplaceservice.repository.LigneCommandeRepository;
import tn.esprit.spring.marketplaceservice.repository.LivraisonRepository;
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
    LivraisonRepository livraisonRepository;
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
    @Override
    @Transactional
    public Commande updateCommandeStatus(Long commandeId,  UpdateStateCommand dto) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + commandeId));
        commande.setEtat(dto.getEtat());
        return commandeRepository.save(commande);
    }

    @Override
    public List<Commande> findByUserId(Long userId) {
        return commandeRepository.findByUserId(userId);
    }

    @Override
    public Commande affecterLivreurACommande(Long commandeId, Long livraisonId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + commandeId));
        Livraison livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new RuntimeException("Livraison not found with id: " + livraisonId));
        commande.setLivraison(livraison);
        return commandeRepository.save(commande);
    }

    @Override
    public List<Commande> findByLivraisonIdLivraison(Long idLivraison) {
        return commandeRepository.findByLivraisonIdLivraison(idLivraison);
    }


}
