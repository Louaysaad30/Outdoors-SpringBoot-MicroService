package tn.esprit.spring.userservice.dto.Request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

@Getter
@Setter
@Builder
public class UserUpdateRequest {
    private String nom;

    private String prenom;

    private int tel;

    private LocalDate dateNaissance;

    private String email;

    private MultipartFile image;
    private String location;
}