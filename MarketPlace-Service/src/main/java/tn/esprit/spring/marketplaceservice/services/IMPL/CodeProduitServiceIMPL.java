package tn.esprit.spring.marketplaceservice.services.IMPL;


import lombok.AllArgsConstructor;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.entity.CodeProduit;
import tn.esprit.spring.marketplaceservice.repository.CodeProduitRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.ICodeProduitService;

import java.util.List;

@AllArgsConstructor
@Service
public class CodeProduitServiceIMPL implements ICodeProduitService {

    CodeProduitRepository codeProduitRepository;
    @Override
    public CodeProduit addCodeProduit(CodeProduit codeProduit) {
        return codeProduitRepository.save(codeProduit);
    }

    @Override
    public CodeProduit retrieveCodeProduit(String code) {
        return codeProduitRepository.findByCode(code);
    }

    @Override
    public void deleteCodeProduit(Long id) {
        codeProduitRepository.deleteById(id);

    }

    @Override
    public CodeProduit updateCodeProduit(CodeProduit codeProduit) {
        return codeProduitRepository.save(codeProduit);
    }

    @Override
    public List<CodeProduit> retrieveCodeProduits() {
        return codeProduitRepository.findAll();
    }
}
