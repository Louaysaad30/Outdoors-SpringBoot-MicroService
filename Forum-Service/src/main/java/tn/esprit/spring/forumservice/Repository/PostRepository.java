package tn.esprit.spring.forumservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.forumservice.entity.Post;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
}
