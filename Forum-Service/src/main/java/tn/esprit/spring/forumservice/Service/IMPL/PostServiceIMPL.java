package tn.esprit.spring.forumservice.Service.IMPL;

           import lombok.RequiredArgsConstructor;
           import org.springframework.http.HttpEntity;
           import org.springframework.http.HttpHeaders;
           import org.springframework.stereotype.Service;
           import org.springframework.web.client.RestTemplate;
           import tn.esprit.spring.forumservice.Config.PerspectiveApiConfig;
           import tn.esprit.spring.forumservice.Repository.CommentRepository;
           import tn.esprit.spring.forumservice.Repository.MediaRepository;
           import tn.esprit.spring.forumservice.Repository.PostRepository;
           import tn.esprit.spring.forumservice.Repository.ReactionRepository;
           import tn.esprit.spring.forumservice.Service.Interfaces.MediaService;
           import tn.esprit.spring.forumservice.Service.Interfaces.PostService;
           import tn.esprit.spring.forumservice.entity.Media;
           import tn.esprit.spring.forumservice.entity.MediaType;
           import tn.esprit.spring.forumservice.entity.Post;

           import java.time.LocalDate;
           import java.time.LocalDateTime;
           import java.util.*;
           import java.util.stream.Collectors;


// Add these fields to the class


@Service
           @RequiredArgsConstructor
           public class PostServiceIMPL implements PostService {
    private final PerspectiveApiConfig perspectiveApiConfig;
    private final RestTemplate restTemplate;
    private static final float TOXICITY_THRESHOLD = 0.7f;

    private final PostRepository postRepository;
               private final MediaService mediaService;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final MediaRepository mediaRepository;


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
            // Log the exception in a real application
            System.err.println("Error checking content toxicity: " + e.getMessage());
            return false;
        }
    }
    @Override
public Post createPost(Post post) {
    // Check content for toxicity
    if (post.getContent() != null && !post.getContent().isEmpty()) {
        boolean isToxic = isContentToxic(post.getContent());
        if (isToxic) {
            throw new RuntimeException("Post content contains inappropriate language");
        }
    }
    return postRepository.save(post);
}




               @Override
               public Post getPostById(UUID id) {
                   return postRepository.findById(id)
                           .orElseThrow(() -> new RuntimeException("Post not found"));
               }

               @Override
               public List<Post> getAllPosts() {
                   return postRepository.findAll();
               }

               @Override
               public Post updatePost(UUID id, Post post) {
                   Post existingPost = getPostById(id);
                   existingPost.setContent(post.getContent());
                   existingPost.setHasMedia(post.getHasMedia());
                   return postRepository.save(existingPost);
               }

               @Override
               public void deletePost(UUID id) {
                   postRepository.deleteById(id);
               }

               @Override
               public List<Post> getPostsByUser(Integer userId) {
                   return postRepository.findByUserIdWithMedia(userId);
               }

               @Override
               public Post updatePost(UUID postId, String content, List<String> mediaUrls, List<String> mediaTypes, List<UUID> mediaToDelete) {
                   Optional<Post> postOptional = postRepository.findById(postId);
                   if (postOptional.isPresent()) {
                       Post post = postOptional.get();

                       // Mettre à jour le texte du post
                       if (content != null) {
                           post.setContent(content);
                       }

                       // Ajouter de nouveaux médias
                       if (mediaUrls != null && !mediaUrls.isEmpty()) {
                           for (int i = 0; i < mediaUrls.size(); i++) {
                               Media media = Media.builder()
                                       .mediaUrl(mediaUrls.get(i))
                                       .mediaType(MediaType.valueOf(mediaTypes.get(i).toUpperCase()))  // Convert String to MediaType enum
                                       .post(post)
                                       .build();
                               mediaService.saveMedia(media);  // Utiliser le service pour enregistrer le média
                               post.getMedia().add(media); // Ajouter le média à la liste des médias du post
                           }
                       }

                       // Supprimer les anciens médias
                       if (mediaToDelete != null && !mediaToDelete.isEmpty()) {
                           for (UUID mediaId : mediaToDelete) {
                               Optional<Media> mediaOptional = post.getMedia().stream()
                                       .filter(media -> media.getId().equals(mediaId))
                                       .findFirst();

                               mediaOptional.ifPresent(media -> {
                                   post.getMedia().remove(media);
                                   mediaService.deleteMedia(media);
                                   post.setHasMedia(false);// Utiliser le service pour supprimer le média
                               });
                           }
                       }

                       // Sauvegarder les changements du post
                       postRepository.save(post);
                       return post;  // Return the updated post
                   }
                   return null;  // Return null if not found
               }

               @Override
               public List<Media> getMediaFromUserPosts(Integer userId) {
                   List<Post> userPosts = getPostsByUser(userId);
                   List<Media> allMedia = new ArrayList<>();

                   for (Post post : userPosts) {
                       if (post.getHasMedia() && post.getMedia() != null) {
                           allMedia.addAll(post.getMedia());
                       }
                   }

                   return allMedia;
               }

               // In PostServiceIMPL.java
               @Override
               public long countPostsByDate(LocalDate date) {
                   return postRepository.countByCreatedAtDate(date);
               }

               @Override
               public long countPostsToday() {
                   return countPostsByDate(LocalDate.now());
               }

               @Override
               public Map<LocalDate, Long> getPostsCountByDay(LocalDate startDate, LocalDate endDate) {
                   LocalDateTime start = startDate.atStartOfDay();
                   LocalDateTime end = endDate.atTime(23, 59, 59);

                   List<Object[]> results = postRepository.countPostsGroupByDate(start, end);

                   Map<LocalDate, Long> countByDay = new LinkedHashMap<>();
                   for (Object[] result : results) {
                       LocalDate date = (LocalDate) result[0];
                       Long count = ((Number) result[1]).longValue();
                       countByDay.put(date, count);
                   }

                   return countByDay;
               }

               // In PostServiceIMPL.java
               @Override
               public long countTotalPosts() {
                   return postRepository.count();
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
    List<Object[]> results = postRepository.countPostsGroupByDate(start, end);

    return results.stream()
            .mapToLong(result -> ((Number) result[1]).longValue())
            .sum();
}

@Override
public List<Map<String, Object>> getTopRatedPostsForCurrentMonth(int limit) {
    // Calculate first and last day of the current month
    LocalDate today = LocalDate.now();
    LocalDate firstDayOfMonth = today.withDayOfMonth(1);
    LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

    // Get timestamp ranges
    LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
    LocalDateTime endOfMonth = lastDayOfMonth.atTime(23, 59, 59);

    // Get all posts
    List<Post> allPosts = postRepository.findAll();

    // Find maximum possible score to normalize ratings
    int maxScore = allPosts.stream()
        .filter(post -> {
            LocalDateTime createdAt = post.getCreatedAt();
            return !createdAt.isBefore(startOfMonth) && !createdAt.isAfter(endOfMonth);
        })
        .mapToInt(post -> {
            int commentCount = post.getComments() != null ? post.getComments().size() : 0;
            int reactionCount = post.getReactions() != null ? post.getReactions().size() : 0;
            return commentCount + reactionCount;
        })
        .max()
        .orElse(1); // Avoid division by zero

    // Filter posts for the current month with ratings
    return allPosts.stream()
        .filter(post -> {
            LocalDateTime createdAt = post.getCreatedAt();
            return !createdAt.isBefore(startOfMonth) && !createdAt.isAfter(endOfMonth);
        })
        .map(post -> {
            int commentCount = post.getComments() != null ? post.getComments().size() : 0;
            int reactionCount = post.getReactions() != null ? post.getReactions().size() : 0;
            int score = commentCount + reactionCount;

            // Calculate rating on a scale of 1-5
            double rating = 1.0;
            if (maxScore > 0) {
                rating = 1.0 + ((double) score / maxScore) * 4.0; // Scale from 1 to 5
            }

            Map<String, Object> postData = new HashMap<>();
            postData.put("post", post);
            postData.put("score", score);
            postData.put("commentCount", commentCount);
            postData.put("reactionCount", reactionCount);
            postData.put("rating", Math.round(rating * 10) / 10.0); // Round to 1 decimal place

            return postData;
        })
        .sorted((p1, p2) -> Double.compare(
            (Double) p2.get("rating"),
            (Double) p1.get("rating")
        ))
        .limit(limit)
        .collect(Collectors.toList());
}


// Add to PostServiceIMPL, CommentServiceIMPL, and ReactionServiceIMPL
@Override
public Map<Integer, Long> getCountByHourForDate(LocalDate date) {
    List<Object[]> results = postRepository.countByHourOfDayForDate(date); // Adjust for each service

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