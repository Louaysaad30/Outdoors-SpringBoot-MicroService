package tn.esprit.spring.userservice.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {
    @Email(message = "email n'est pas dans bon format")
    @NotEmpty(message = "email is required")
    @NotBlank(message = "email is required")
    String email;

    @NotEmpty(message = "mdp is required")
    @NotBlank(message = "mdp is required")
    @Size(min =8 ,message = "mdp minimun 8 caractéres")
    String motDePasse;
    String recaptchaToken; // <-- ajouté

}
