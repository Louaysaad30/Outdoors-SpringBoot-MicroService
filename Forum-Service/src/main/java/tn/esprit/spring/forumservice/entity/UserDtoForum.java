package tn.esprit.spring.forumservice.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoForum {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private String image;
}
