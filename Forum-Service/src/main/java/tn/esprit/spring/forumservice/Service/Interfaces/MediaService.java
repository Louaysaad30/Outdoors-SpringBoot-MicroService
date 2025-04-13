package tn.esprit.spring.forumservice.Service.Interfaces;

import tn.esprit.spring.forumservice.entity.Media;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MediaService {
    void saveMedia(Media media);

     void deleteMedia(Media media) ;
    List<Media> getMediaByUserId(Integer userId);

    long countMediaByDate(LocalDate date);
    long countMediaToday();
    Map<LocalDate, Long> getMediaCountByDay(LocalDate startDate, LocalDate endDate);
    long countTotalMedia();

    // Add to PostService.java, CommentService.java, ReactionService.java, MediaService.java
    Map<String, Object> getWeeklyChangePercentage();
}
