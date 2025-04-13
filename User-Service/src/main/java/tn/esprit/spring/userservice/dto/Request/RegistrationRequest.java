package tn.esprit.spring.userservice.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "nom is required")
    @NotBlank(message = "nom is required")
    String nom;

    @NotEmpty(message = "prenom is required")
    @NotBlank(message = "prenom is required")
    String prenom;

    @Email(message = "email n'est pas dans bon format")
    @NotEmpty(message = "email is required")
    @NotBlank(message = "email is required")
    String email;

    @NotEmpty(message = "mdp is required")
    @NotBlank(message = "mdp is required")
    @Size(min =8 ,message = "mdp minimun 8 caractéres")
    String motDePasse;

    MultipartFile image;
    int tel;
    LocalDate dateNaissance;
    private String role;
    private String location; // only for AGENT



}
