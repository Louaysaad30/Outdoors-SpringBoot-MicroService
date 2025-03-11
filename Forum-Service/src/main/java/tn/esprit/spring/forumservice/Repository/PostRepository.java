package tn.esprit.spring.forumservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.forumservice.entity.Post;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByUserId(Integer userId);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.media WHERE p.userId = :userId")
    List<Post> findByUserIdWithMedia(@Param("userId") Integer userId);


}
