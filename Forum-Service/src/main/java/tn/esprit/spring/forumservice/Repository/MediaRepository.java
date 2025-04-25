package tn.esprit.spring.forumservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.forumservice.entity.Media;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MediaRepository extends JpaRepository <Media, UUID> {

    List<Media> findByUserId(Integer userId);

    @Query("SELECT COUNT(m) FROM Media m WHERE CAST(m.createdAt AS LocalDate) = :date")
    long countByCreatedAtDate(@Param("date") LocalDate date);

    @Query("SELECT CAST(m.createdAt AS LocalDate) AS date, COUNT(m) AS count FROM Media m " +
           "WHERE m.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(m.createdAt AS LocalDate) " +
           "ORDER BY CAST(m.createdAt AS LocalDate)")
    List<Object[]> countMediaGroupByDate(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);


}
