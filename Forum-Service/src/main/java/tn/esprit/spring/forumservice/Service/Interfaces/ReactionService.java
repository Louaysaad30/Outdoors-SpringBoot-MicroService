package tn.esprit.spring.forumservice.Service.Interfaces;

import tn.esprit.spring.forumservice.entity.Reaction;
import tn.esprit.spring.forumservice.entity.ReactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReactionService {

    Reaction addReaction(UUID postId, Integer userId, ReactionType reactionType);
    void deleteReaction(UUID reactionId);
    Reaction updateReaction(UUID reactionId, ReactionType newReactionType);
    List<Reaction> getReactionsByPostId(UUID postId);
    Reaction getUserReactionOnPost(UUID postId, Integer userId);
    long countReactionsByDate(LocalDate date);
    long countReactionsToday();
    Map<LocalDate, Long> getReactionsCountByDay(LocalDate startDate, LocalDate endDate);
    long countTotalReactions();

    // Add to PostService.java, CommentService.java, ReactionService.java, MediaService.java
    Map<String, Object> getWeeklyChangePercentage();

    // Add to PostService, CommentService, and ReactionService interfaces
    Map<Integer, Long> getCountByHourForDate(LocalDate date);

}
