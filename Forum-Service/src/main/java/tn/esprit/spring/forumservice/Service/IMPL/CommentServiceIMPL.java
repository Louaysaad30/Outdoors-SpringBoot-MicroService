package tn.esprit.spring.forumservice.Service.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.spring.forumservice.Config.PerspectiveApiConfig;
import tn.esprit.spring.forumservice.Repository.CommentRepository;
import tn.esprit.spring.forumservice.Repository.PostRepository;
import tn.esprit.spring.forumservice.Service.Interfaces.CommentService;
import tn.esprit.spring.forumservice.entity.Comment;
import tn.esprit.spring.forumservice.entity.Post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentServiceIMPL implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PerspectiveApiConfig perspectiveApiConfig;
    private final RestTemplate restTemplate;
    private static final float TOXICITY_THRESHOLD = 0.7f;

    private boolean isContentToxic(String content) {
        try {
            if (content == null || content.isEmpty()) {
                return false;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> comment = new HashMap<>();
            comment.put("text", content);
            requestBody.put("comment", comment);

            Map<String, Object> requestedAttributes = new HashMap<>();
            Map<String, Object> toxicity = new HashMap<>();
            toxicity.put("scoreThreshold", 0.0);
            requestedAttributes.put("TOXICITY", toxicity);

            requestBody.put("requestedAttributes", requestedAttributes);

            // Add API key to URL
            String url = perspectiveApiConfig.getApiUrl() + "?key=" + perspectiveApiConfig.getApiKey();

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Make API call
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

            // Process response
            if (response != null && response.containsKey("attributeScores")) {
                Map<String, Object> attributeScores = (Map<String, Object>) response.get("attributeScores");
                Map<String, Object> toxicityScore = (Map<String, Object>) attributeScores.get("TOXICITY");
                Map<String, Object> summaryScore = (Map<String, Object>) toxicityScore.get("summaryScore");
                double score = (double) summaryScore.get("value");

                return score >= TOXICITY_THRESHOLD;
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error checking content toxicity: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Comment addComment(UUID postId, String content, Integer userId) {
        // Check content for toxicity
        if (content != null && !content.isEmpty()) {
            boolean isToxic = isContentToxic(content);
            if (isToxic) {
                throw new RuntimeException("Comment content contains inappropriate language");
            }
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Comment comment = Comment.builder()
                .content(content)
                .userId(userId)
                .post(post)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }
    @Override
    public Comment getCommentById(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
    }

    @Override
    public List<Comment> getCommentsByPostId(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // This would be more efficient with a custom query in CommentRepository
        return post.getComments();
    }

    @Override
    public Comment updateComment(UUID id, String content) {
        Comment comment = getCommentById(id);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(UUID id) {
        commentRepository.deleteById(id);
    }


    @Override
public Comment replyToComment(UUID parentCommentId, String content, Integer userId) {
    // Check content for toxicity
    if (content != null && !content.isEmpty()) {
        boolean isToxic = isContentToxic(content);
        if (isToxic) {
            throw new RuntimeException("Comment content contains inappropriate language");
        }
    }

    Comment parentComment = getCommentById(parentCommentId);
    Post post = parentComment.getPost();

    Comment reply = Comment.builder()
            .content(content)
            .userId(userId)
            .post(post)
            .parentComment(parentComment)
            .createdAt(LocalDateTime.now())
            .build();

    return commentRepository.save(reply);
}



@Override
public List<Comment> getTopLevelComments(UUID postId) {
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

    // Filter comments where parentComment is null
    return post.getComments().stream()
            .filter(comment -> comment.getParentComment() == null)
            .toList();
}

// In CommentServiceIMPL.java
@Override
public long countCommentsByDate(LocalDate date) {
    return commentRepository.countByCreatedAtDate(date);
}

@Override
public long countCommentsToday() {
    return countCommentsByDate(LocalDate.now());
}

@Override
public Map<LocalDate, Long> getCommentsCountByDay(LocalDate startDate, LocalDate endDate) {
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.atTime(23, 59, 59);

    List<Object[]> results = commentRepository.countCommentsGroupByDate(start, end);

    Map<LocalDate, Long> countByDay = new LinkedHashMap<>();
    for (Object[] result : results) {
        LocalDate date = (LocalDate) result[0];
        Long count = ((Number) result[1]).longValue();
        countByDay.put(date, count);
    }

    return countByDay;
}
    // In CommentServiceIMPL.java
    @Override
    public long countTotalComments() {
        return commentRepository.count();
    }

    // Add to all service implementations (PostServiceIMPL, CommentServiceIMPL, ReactionServiceIMPL, MediaServiceIMPL)
    @Override
    public Map<String, Object> getWeeklyChangePercentage() {
        // Get current week dates
        LocalDate today = LocalDate.now();
        LocalDate startOfCurrentWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endOfCurrentWeek = today;

        // Get previous week dates
        LocalDate startOfPreviousWeek = startOfCurrentWeek.minusWeeks(1);
        LocalDate endOfPreviousWeek = startOfCurrentWeek.minusDays(1);

        // Count for current week
        long currentWeekCount = countByDateRange(startOfCurrentWeek, endOfCurrentWeek);

        // Count for previous week
        long previousWeekCount = countByDateRange(startOfPreviousWeek, endOfPreviousWeek);

        // Calculate percentage change
        double percentageChange = 0;
        if (previousWeekCount > 0) {
            percentageChange = ((double) (currentWeekCount - previousWeekCount) / previousWeekCount) * 100;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("currentWeek", currentWeekCount);
        result.put("previousWeek", previousWeekCount);
        result.put("percentageChange", Math.round(percentageChange * 100.0) / 100.0); // Round to 2 decimal places
        result.put("increased", currentWeekCount >= previousWeekCount);

        return result;
    }

    // Helper method for each service implementation
    private long countByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        // Use the existing repository method
        List<Object[]> results = commentRepository.countCommentsGroupByDate(start, end);

        return results.stream()
                .mapToLong(result -> ((Number) result[1]).longValue())
                .sum();
    }

    // Add to PostServiceIMPL, CommentServiceIMPL, and ReactionServiceIMPL
    @Override
    public Map<Integer, Long> getCountByHourForDate(LocalDate date) {
        List<Object[]> results = commentRepository.countByHourOfDayForDate(date); // Adjust for each service

        Map<Integer, Long> countByHour = new LinkedHashMap<>();
        // Initialize all hours with 0 count
        for (int i = 0; i < 24; i++) {
            countByHour.put(i, 0L);
        }

        // Fill with actual data
        for (Object[] result : results) {
            Integer hour = ((Number) result[0]).intValue();
            Long count = ((Number) result[1]).longValue();
            countByHour.put(hour, count);
        }

        return countByHour;
    }
}