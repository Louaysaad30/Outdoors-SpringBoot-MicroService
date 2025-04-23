package tn.esprit.spring.userservice.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.esprit.spring.userservice.Entity.Role;
import tn.esprit.spring.userservice.Enum.Etat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User  implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String nom;
    String prenom;
    @Column(unique = true)
    String email;
    String motDePasse;
    String image;
    Boolean status;
    int tel;
    LocalDate dateNaissance;
    private String location;
    @Enumerated(EnumType.STRING)
    Etat etat;
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    List<Role> roles;
    boolean accountLocked;
    boolean enabled;
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    UserDetail userDetail;
    @Override
    //@JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return Collections.emptyList(); // avoids the NPE
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleType().toString()))
                .collect(Collectors.toList());
    }


    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    public String fullName(){
        return nom+" "+prenom;
    }

}

