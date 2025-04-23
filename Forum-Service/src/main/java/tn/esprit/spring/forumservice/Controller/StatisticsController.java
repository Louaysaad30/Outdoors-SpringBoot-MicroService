package tn.esprit.spring.forumservice.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.forumservice.Service.Interfaces.CommentService;
import tn.esprit.spring.forumservice.Service.Interfaces.MediaService;
import tn.esprit.spring.forumservice.Service.Interfaces.PostService;
import tn.esprit.spring.forumservice.Service.Interfaces.ReactionService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class StatisticsController {

    private final PostService postService;
    private final CommentService commentService;
    private final ReactionService reactionService;
    private final MediaService mediaService;

    @GetMapping("/today")
    public ResponseEntity<Map<String, Long>> getTodayStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("postsToday", postService.countPostsToday());
        stats.put("commentsToday", commentService.countCommentsToday());
        stats.put("reactionsToday", reactionService.countReactionsToday());
        stats.put("mediaToday", mediaService.countMediaToday());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/by-date")
    public ResponseEntity<Map<String, Long>> getStatisticsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("posts", postService.countPostsByDate(date));
        stats.put("comments", commentService.countCommentsByDate(date));
        stats.put("reactions", reactionService.countReactionsByDate(date));
        stats.put("media", mediaService.countMediaByDate(date));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Map<LocalDate, Long>>> getDailyStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Map<LocalDate, Long>> stats = new HashMap<>();
        stats.put("posts", postService.getPostsCountByDay(startDate, endDate));
        stats.put("comments", commentService.getCommentsCountByDay(startDate, endDate));
        stats.put("reactions", reactionService.getReactionsCountByDay(startDate, endDate));
        stats.put("media", mediaService.getMediaCountByDay(startDate, endDate));
        return ResponseEntity.ok(stats);
    }

@GetMapping("/total")
public ResponseEntity<Map<String, Long>> getTotalStatistics() {
    Map<String, Long> stats = new HashMap<>();
    stats.put("totalPosts", postService.countTotalPosts());
    stats.put("totalComments", commentService.countTotalComments());
    stats.put("totalReactions", reactionService.countTotalReactions());
    stats.put("totalMedia", mediaService.countTotalMedia());
    return ResponseEntity.ok(stats);
}

@GetMapping("/weekly-trends")
public ResponseEntity<Map<String, Map<String, Object>>> getWeeklyTrends() {
    Map<String, Map<String, Object>> trends = new HashMap<>();

    trends.put("posts", postService.getWeeklyChangePercentage());
    trends.put("comments", commentService.getWeeklyChangePercentage());
    trends.put("reactions", reactionService.getWeeklyChangePercentage());
    trends.put("media", mediaService.getWeeklyChangePercentage());

    return ResponseEntity.ok(trends);
}



@GetMapping("/peak-hours-by-date")
public ResponseEntity<Map<String, Object>> getPeakActivityHoursByDate(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    // Get hourly activity counts for the specified date
    Map<Integer, Long> postsByHour = postService.getCountByHourForDate(date);
    Map<Integer, Long> commentsByHour = commentService.getCountByHourForDate(date);
    Map<Integer, Long> reactionsByHour = reactionService.getCountByHourForDate(date);

    // Aggregate all activities
    Map<Integer, Long> totalByHour = new LinkedHashMap<>();
    Map<Integer, Map<String, Long>> detailsByHour = new LinkedHashMap<>();

    for (int i = 0; i < 24; i++) {
        long postCount = postsByHour.getOrDefault(i, 0L);
        long commentCount = commentsByHour.getOrDefault(i, 0L);
        long reactionCount = reactionsByHour.getOrDefault(i, 0L);
        long total = postCount + commentCount + reactionCount;

        totalByHour.put(i, total);

        Map<String, Long> details = new HashMap<>();
        details.put("posts", postCount);
        details.put("comments", commentCount);
        details.put("reactions", reactionCount);
        details.put("total", total);

        detailsByHour.put(i, details);
    }

    // Find peak hour
    int peakHour = 0;
    long maxCount = 0;

    for (Map.Entry<Integer, Long> entry : totalByHour.entrySet()) {
        if (entry.getValue() > maxCount) {
            maxCount = entry.getValue();
            peakHour = entry.getKey();
        }
    }

    Map<String, Object> result = new HashMap<>();
    result.put("date", date.toString());
    result.put("peakHour", peakHour);
    result.put("peakHourFormatted", String.format("%02d:00", peakHour));
    result.put("peakCount", maxCount);
    result.put("hourlyBreakdown", detailsByHour);

    return ResponseEntity.ok(result);
}


}