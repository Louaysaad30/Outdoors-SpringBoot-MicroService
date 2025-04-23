package tn.esprit.spring.forumservice.Config;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
public class ContentTracker {
    private final Map<String, String> contentUrlMap = new ConcurrentHashMap<>();

    public void addContentUrl(String postId, String contentUrl) {
        contentUrlMap.put(postId, contentUrl);
    }

    public String getContentUrl(String postId) {
        return contentUrlMap.get(postId);
    }

    public boolean hasContent(String postId) {
        return contentUrlMap.containsKey(postId);
    }
}