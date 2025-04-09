package tn.esprit.spring.userservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.userservice.Entity.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findByToken(String token);
}
