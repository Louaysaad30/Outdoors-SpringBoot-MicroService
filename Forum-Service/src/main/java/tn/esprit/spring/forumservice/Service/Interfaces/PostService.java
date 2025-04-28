package tn.esprit.spring.forumservice.Service.Interfaces;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.forumservice.entity.Media;
import tn.esprit.spring.forumservice.entity.Post;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PostService {
    Post createPost(Post post);

    Post getPostById(UUID id);
    List<Post> getAllPosts();
    Post updatePostWithMedia(UUID postId, String content, List<Media> newMedia, List<UUID> mediaToDelete);

    Post updatePost(UUID id, Post post);
    void deletePost(UUID id);
    List<Post> getPostsByUser(Integer userId);
    Post  updatePost(UUID postId, String content, List<String> mediaUrls, List<String> mediaTypes, List<UUID> mediaToDelete) ;
    List<Media> getMediaFromUserPosts(Integer userId);

    // In PostService.java
    long countPostsByDate(LocalDate date);
    long countPostsToday();
    Map<LocalDate, Long> getPostsCountByDay(LocalDate startDate, LocalDate endDate);


    // Add to PostService.java
    long countTotalPosts();

    // Add to PostService.java, CommentService.java, ReactionService.java, MediaService.java
    Map<String, Object> getWeeklyChangePercentage();
   // Add to PostService.java
   List<Map<String, Object>> getTopRatedPostsForCurrentMonth(int limit) ;


   // Add to PostService, CommentService, and ReactionService interfaces
   Map<Integer, Long> getCountByHourForDate(LocalDate date);
    }
