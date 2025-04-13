package tn.esprit.spring.forumservice.Service.Interfaces;

import tn.esprit.spring.forumservice.entity.Comment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CommentService {
    Comment addComment(UUID postId, String content, Integer userId);
    Comment getCommentById(UUID id);
    List<Comment> getCommentsByPostId(UUID postId);
    Comment updateComment(UUID id, String content);
    void deleteComment(UUID id);
    Comment replyToComment(UUID parentCommentId, String content, Integer userId);
    List<Comment> getTopLevelComments(UUID postId);

    // In CommentService.java
    long countCommentsByDate(LocalDate date);
    long countCommentsToday();
    Map<LocalDate, Long> getCommentsCountByDay(LocalDate startDate, LocalDate endDate);

    long countTotalComments();

    // Add to PostService.java, CommentService.java, ReactionService.java, MediaService.java
    Map<String, Object> getWeeklyChangePercentage();


    // Add to PostService, CommentService, and ReactionService interfaces
    Map<Integer, Long> getCountByHourForDate(LocalDate date);
}

