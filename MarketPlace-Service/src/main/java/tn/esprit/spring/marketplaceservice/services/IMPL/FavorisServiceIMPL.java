package tn.esprit.spring.marketplaceservice.services.IMPL;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.entity.Favoris;
import tn.esprit.spring.marketplaceservice.repository.FavorisRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.IFavorisService;

import java.util.List;

@AllArgsConstructor
@Service
public class FavorisServiceIMPL implements IFavorisService {

    FavorisRepository favorisRepository;

    @Override
    public List<Favoris> retrieveAllFavoris() {
        return favorisRepository.findAll();
    }

    @Override
    public Favoris addFavoris(Favoris favoris) {
        return favorisRepository.save(favoris);
    }

    @Override
    public Favoris retrieveFavoris(long idFavoris) {
        return favorisRepository.findById(idFavoris).orElse(null);
    }

    @Override
    public void removeFavoris(long idFavoris) {
        favorisRepository.deleteById(idFavoris);
    }

    @Override
    public Favoris updateFavoris(Favoris favoris) {
        return favorisRepository.save(favoris);
    }

    @Override
    public List<Favoris> retrieveFavorisByUserId(Long userId) {
        return favorisRepository.findByIdUser(userId);
    }





}
