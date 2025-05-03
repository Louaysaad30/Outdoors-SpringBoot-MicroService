package tn.esprit.spring.transportservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.spring.transportservice.entity.BadWord;

import java.util.List;

public interface BadWordRepository extends JpaRepository<BadWord, Long> {
    @Query("SELECT b.word FROM BadWord b")
    List<String> findAllWords();  // Returns words as strings

    boolean existsByWord(String word);
}