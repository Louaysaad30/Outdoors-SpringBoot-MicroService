package tn.esprit.spring.forumservice.Service.IMPL;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.forumservice.Repository.PostRepository;
import tn.esprit.spring.forumservice.Repository.ReactionRepository;
import tn.esprit.spring.forumservice.Service.Interfaces.ReactionService;
import tn.esprit.spring.forumservice.entity.Post;
import tn.esprit.spring.forumservice.entity.Reaction;
import tn.esprit.spring.forumservice.entity.ReactionType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReactionServiceIMPL implements ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;

    @Override
    public Reaction addReaction(UUID postId, Integer userId, ReactionType reactionType) {
        // Find the post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // Check if user already has a reaction on this post
        Optional<Reaction> existingReaction = post.getReactions().stream()
                .filter(r -> r.getUserId().equals(userId))
                .findFirst();

        if (existingReaction.isPresent()) {
            // Update existing reaction
            Reaction reaction = existingReaction.get();
            reaction.setReactionType(reactionType);
            reaction.setCreatedAt(LocalDateTime.now());
            return reactionRepository.save(reaction);
        } else {
            // Create new reaction
            Reaction reaction = Reaction.builder()
                    .userId(userId)
                    .post(post)
                    .reactionType(reactionType)
                    .createdAt(LocalDateTime.now())
                    .build();
            return reactionRepository.save(reaction);
        }
    }

    @Override
    public void deleteReaction(UUID reactionId) {
        reactionRepository.deleteById(reactionId);
    }

    @Override
    public Reaction updateReaction(UUID reactionId, ReactionType newReactionType) {
        Reaction reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new RuntimeException("Reaction not found with id: " + reactionId));
        reaction.setReactionType(newReactionType);
        reaction.setCreatedAt(LocalDateTime.now());
        return reactionRepository.save(reaction);
    }

    @Override
    public List<Reaction> getReactionsByPostId(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        return post.getReactions();
    }

    @Override
    public Reaction getUserReactionOnPost(UUID postId, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        return post.getReactions().stream()
                .filter(r -> r.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }


    @Override
    public long countReactionsByDate(LocalDate date) {
        return reactionRepository.countByCreatedAtDate(date);
    }

    @Override
    public long countReactionsToday() {
        return countReactionsByDate(LocalDate.now());
    }

    @Override
    public Map<LocalDate, Long> getReactionsCountByDay(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Object[]> results = reactionRepository.countReactionsGroupByDate(start, end);

        Map<LocalDate, Long> countByDay = new LinkedHashMap<>();
        for (Object[] result : results) {
            LocalDate date = (LocalDate) result[0];
            Long count = ((Number) result[1]).longValue();
            countByDay.put(date, count);
        }

        return countByDay;
    }

    @Override
    public long countTotalReactions() {
        return reactionRepository.count();
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
        List<Object[]> results = reactionRepository.countReactionsGroupByDate(start, end);

        return results.stream()
                .mapToLong(result -> ((Number) result[1]).longValue())
                .sum();
    }

    // Add to PostServiceIMPL, CommentServiceIMPL, and ReactionServiceIMPL
    @Override
    public Map<Integer, Long> getCountByHourForDate(LocalDate date) {
        List<Object[]> results = reactionRepository.countByHourOfDayForDate(date); // Adjust for each service

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
