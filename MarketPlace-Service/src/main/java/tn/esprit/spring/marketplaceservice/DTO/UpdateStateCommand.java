package tn.esprit.spring.marketplaceservice.DTO;

import tn.esprit.spring.marketplaceservice.entity.Status;

import lombok.Data;

@Data
public class UpdateStateCommand
{
    private Long ididCommande;
    private Status etat;
}
