package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.entity.CodeProduit;

public interface ICodeProduitService {
    CodeProduit addCodeProduit(CodeProduit codeProduit);
    CodeProduit retrieveCodeProduit(String code);
    void deleteCodeProduit(Long id);
    CodeProduit updateCodeProduit(CodeProduit codeProduit);

}
