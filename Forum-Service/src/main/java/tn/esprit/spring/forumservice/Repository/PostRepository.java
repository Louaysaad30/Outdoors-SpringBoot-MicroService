package tn.esprit.spring.forumservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.forumservice.entity.Post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByUserId(Integer userId);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.media WHERE p.userId = :userId")
    List<Post> findByUserIdWithMedia(@Param("userId") Integer userId);


    // In PostRepository.java
    @Query("SELECT COUNT(p) FROM Post p WHERE CAST(p.createdAt AS LocalDate) = :date")
    long countByCreatedAtDate(@Param("date") LocalDate date);

    @Query("SELECT CAST(p.createdAt AS LocalDate) AS date, COUNT(p) AS count FROM Post p " +
           "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(p.createdAt AS LocalDate) " +
           "ORDER BY CAST(p.createdAt AS LocalDate)")
    List<Object[]> countPostsGroupByDate(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);


    // Add to PostRepository, CommentRepository, and ReactionRepository
    @Query("SELECT EXTRACT(HOUR FROM p.createdAt) AS hour, COUNT(p) AS count FROM Post p " +
           "WHERE FUNCTION('DATE', p.createdAt) = :date " +
           "GROUP BY EXTRACT(HOUR FROM p.createdAt) ORDER BY EXTRACT(HOUR FROM p.createdAt)")
    List<Object[]> countByHourOfDayForDate(@Param("date") LocalDate date);

}
