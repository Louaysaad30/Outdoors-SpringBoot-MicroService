package tn.esprit.spring.forumservice.Service.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.forumservice.Repository.MediaRepository;
import tn.esprit.spring.forumservice.Service.Interfaces.MediaService;
import tn.esprit.spring.forumservice.entity.Media;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediaServiceIMPL implements MediaService {
    private final MediaRepository mediaRepository;

    @Override
    public void saveMedia(Media media) {
        mediaRepository.save(media);
    }



    @Override
    public void deleteMedia(Media media) {
        mediaRepository.delete(media);

    }

    @Override
    public List<Media> getMediaByUserId(Integer userId) {
        return mediaRepository.findByUserId(userId);
    }

    @Override
    public long countMediaByDate(LocalDate date) {
        return mediaRepository.countByCreatedAtDate(date);
    }

    @Override
    public long countMediaToday() {
        return countMediaByDate(LocalDate.now());
    }

    @Override
    public Map<LocalDate, Long> getMediaCountByDay(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Object[]> results = mediaRepository.countMediaGroupByDate(start, end);

        Map<LocalDate, Long> countByDay = new LinkedHashMap<>();
        for (Object[] result : results) {
            LocalDate date = (LocalDate) result[0];
            Long count = ((Number) result[1]).longValue();
            countByDay.put(date, count);
        }

        return countByDay;
    }

    // In MediaServiceIMPL.java
    @Override
    public long countTotalMedia() {
        return mediaRepository.count();
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
        List<Object[]> results = mediaRepository.countMediaGroupByDate(start, end);

        return results.stream()
                .mapToLong(result -> ((Number) result[1]).longValue())
                .sum();
    }
}
