package tn.esprit.spring.forumservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.forumservice.entity.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

// In CommentRepository.java
@Query("SELECT COUNT(c) FROM Comment c WHERE CAST(c.createdAt AS LocalDate) = :date")
long countByCreatedAtDate(@Param("date") LocalDate date);

@Query("SELECT CAST(c.createdAt AS LocalDate) AS date, COUNT(c) AS count FROM Comment c " +
       "WHERE c.createdAt BETWEEN :startDate AND :endDate " +
       "GROUP BY CAST(c.createdAt AS LocalDate) " +
       "ORDER BY CAST(c.createdAt AS LocalDate)")
List<Object[]> countCommentsGroupByDate(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);



// Add to PostRepository, CommentRepository, and ReactionRepository
@Query("SELECT EXTRACT(HOUR FROM p.createdAt) AS hour, COUNT(p) AS count FROM Comment p " +
       "WHERE FUNCTION('DATE', p.createdAt) = :date " +
       "GROUP BY EXTRACT(HOUR FROM p.createdAt) ORDER BY EXTRACT(HOUR FROM p.createdAt)")
List<Object[]> countByHourOfDayForDate(@Param("date") LocalDate date);
}
