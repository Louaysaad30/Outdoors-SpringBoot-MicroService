package tn.esprit.spring.marketplaceservice.services.IMPL;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.DTO.DeliveryStatusUpdateDto;
import tn.esprit.spring.marketplaceservice.entity.Livraison;
import tn.esprit.spring.marketplaceservice.repository.LivraisonRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.ILivraisonService;

import java.util.List;

@AllArgsConstructor
@Service
public class LivraisonServiceIMPL implements ILivraisonService {
    LivraisonRepository livraisonRepository;
    @Override
    public List<Livraison> retrieveLivraisons() {
        return livraisonRepository.findAll();
    }

    @Override
    public Livraison updateLivraison(Livraison livraison) {
        return livraisonRepository.save(livraison);
    }

    @Override
    public Livraison retrieveLivraison(long idLivraison) {
        return livraisonRepository.findById(idLivraison).orElse(null);
    }

    @Override
    public void removeLivraison(long idLivraison) {
        livraisonRepository.deleteById(idLivraison);
    }

    @Override
    public List<Livraison> findByLivreurId(Long livreurId) {
        return livraisonRepository.findByLivreurId(livreurId);
    }

    @Override
    public Livraison addLivraison(Livraison livraison) {
        return livraisonRepository.save(livraison);
    }

    @Override
    public Livraison updateLivraisonStatus(Long idLivraison, DeliveryStatusUpdateDto dto) {
        Livraison livraison = livraisonRepository.findById(idLivraison).orElse(null);
            livraison.setEtatLivraison(dto.getEtatLivraison());
            return livraisonRepository.save(livraison);
    }


}
