package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.entity.Favoris;

import java.util.List;

public interface IFavorisService {
    List<Favoris> retrieveAllFavoris();
    Favoris addFavoris(Favoris favoris);
    Favoris updateFavoris(Favoris favoris);
    Favoris retrieveFavoris(long idFavoris);
    void removeFavoris(long idFavoris);

    List<Favoris> retrieveFavorisByUserId(Long userId);

}
