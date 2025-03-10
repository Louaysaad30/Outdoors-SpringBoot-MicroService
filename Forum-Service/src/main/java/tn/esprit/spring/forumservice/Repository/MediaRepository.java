package tn.esprit.spring.forumservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.forumservice.entity.Media;

import java.util.UUID;

public interface MediaRepository extends JpaRepository <Media, UUID> {
}
