package tn.esprit.spring.forumservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.forumservice.entity.Reaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    List<Reaction> findByPostId(UUID postId);
    Optional<Reaction> findByPostIdAndUserId(UUID postId, Integer userId);
    void deleteByPostIdAndUserId(UUID postId, Integer userId);

@Query("SELECT COUNT(r) FROM Reaction r WHERE CAST(r.createdAt AS LocalDate) = :date")
long countByCreatedAtDate(@Param("date") LocalDate date);

@Query("SELECT CAST(r.createdAt AS LocalDate) AS date, COUNT(r) AS count FROM Reaction r " +
       "WHERE r.createdAt BETWEEN :startDate AND :endDate " +
       "GROUP BY CAST(r.createdAt AS LocalDate) " +
       "ORDER BY CAST(r.createdAt AS LocalDate)")
List<Object[]> countReactionsGroupByDate(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);


// Add to ReactionRepository
@Query("SELECT EXTRACT(HOUR FROM r.createdAt) AS hour, COUNT(r) AS count FROM Reaction r " +
       "WHERE FUNCTION('DATE', r.createdAt) = :date " +
       "GROUP BY EXTRACT(HOUR FROM r.createdAt) ORDER BY EXTRACT(HOUR FROM r.createdAt)")
List<Object[]> countByHourOfDayForDate(@Param("date") LocalDate date);
}
