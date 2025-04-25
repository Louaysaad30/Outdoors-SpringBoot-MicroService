package tn.esprit.spring.userservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Enum.Etat;
import tn.esprit.spring.userservice.Enum.RoleType;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User>findByEmail(String email);
    Optional<User>findById(Long id);

    List<User> findByRolesRoleType(RoleType roleType);

    List<User> findAllByEtat(Etat etat);

}
