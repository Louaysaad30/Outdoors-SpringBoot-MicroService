package tn.esprit.spring.forumservice.Controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.forumservice.Config.ContentTracker;
import tn.esprit.spring.forumservice.Service.API.ServiceAPI;
import tn.esprit.spring.forumservice.Service.IMPL.UserService;
import tn.esprit.spring.forumservice.Service.Interfaces.MediaService;
import tn.esprit.spring.forumservice.Service.Interfaces.PostService;
import tn.esprit.spring.forumservice.entity.Media;
import tn.esprit.spring.forumservice.entity.MediaType;
import tn.esprit.spring.forumservice.entity.Post;
import tn.esprit.spring.forumservice.entity.UserDtoForum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final PostService postService;
    private final MediaService mediaService;
    private final Cloudinary cloudinary;
    private final ServiceAPI serviceAPI;
    private final ContentTracker contentTracker;
    private final UserService userService;
    private final RestTemplate restTemplate;

    @GetMapping("/all")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

@PostMapping(value = "/add", consumes = "multipart/form-data")
public ResponseEntity<?> createPost(@RequestParam("content") String content,
                                   @RequestParam("userId") Integer userId,
                                   @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles) {
    try {
        // First check if content is appropriate
        if (content != null && !content.isEmpty() && serviceAPI.isContentToxic(content)) {
            return ResponseEntity.badRequest()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(Map.of("message", "TOXIC_CONTENT"));
        }

        // Create and save post first without media
        Post post = new Post();
        post.setContent(content);
        post.setUserId(userId);
        post.setCreatedAt(java.time.LocalDateTime.now());
        post.setHasMedia(mediaFiles != null && !mediaFiles.isEmpty());

        // Save post first to get an ID
        Post savedPost = postService.createPost(post);

        // Process media files if present
        List<String> uploadedPublicIds = new ArrayList<>();

        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            for (MultipartFile file : mediaFiles) {
                try {
                    boolean isAppropriate = true;

                    // For images, check appropriateness
                    if (file.getContentType().contains("image")) {
                        // Upload temporary image for checking
                        Map tempUploadResult = cloudinary.uploader().upload(
                                file.getBytes(),
                                ObjectUtils.asMap(
                                        "resource_type", "auto",
                                        "folder", "temp_moderation"
                                )
                        );

                        String tempUrl = (String) tempUploadResult.get("url");
                        String tempPublicId = (String) tempUploadResult.get("public_id");

                        // Check if image is appropriate
                        isAppropriate = serviceAPI.isImageAppropriate(tempUrl);

                        // Delete the temporary image
                        cloudinary.uploader().destroy(tempPublicId, ObjectUtils.emptyMap());

                        if (!isAppropriate) {
                            // Clean up files and delete post
                            for (String publicId : uploadedPublicIds) {
                                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                            }
                            postService.deletePost(savedPost.getId());

                            return ResponseEntity.badRequest()
                                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                    .body(Map.of("message", "TOXIC_IMAGE"));
                        }
                    }

                    // Upload file to final destination
                    Map uploadResult = cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap("resource_type", "auto")
                    );

                    String fileUrl = (String) uploadResult.get("url");
                    String publicId = (String) uploadResult.get("public_id");
                    uploadedPublicIds.add(publicId);

                    // Create and save media with post reference
                    Media media = new Media();
                    media.setMediaUrl(fileUrl);
                    media.setUserId(userId);
                    media.setMediaType(file.getContentType().contains("image") ?
                                      MediaType.IMAGE : MediaType.VIDEO);
                    media.setPost(savedPost);  // Set post reference

                    // Save media directly
                    mediaService.saveMedia(media);

                } catch (Exception e) {
                    // Clean up files and delete post on error
                    for (String publicId : uploadedPublicIds) {
                        try {
                            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                        } catch (Exception cleanupError) {
                            // Continue cleanup
                        }
                    }
                    postService.deletePost(savedPost.getId());
                    throw e;
                }
            }
        }

        // Get refreshed post with media loaded
        Post refreshedPost = postService.getPostById(savedPost.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(refreshedPost);
    } catch (Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
    }
}




    @GetMapping("/user/{userId}")
    public List<Post> getUserPosts(@PathVariable Integer userId) {
        return postService.getPostsByUser(userId);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostDetails(@PathVariable("postId") UUID postId) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // After
    @ApiOperation(value = "Mettre à jour un post", notes = "Cette méthode permet de mettre à jour le contenu d'un post et d'ajouter de nouveaux médias.")
    @PutMapping(value = "/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<?> updatePost(
            @ApiParam(value = "ID du post à mettre à jour", required = true) @PathVariable UUID postId,
            @ApiParam(value = "Nouveau contenu du post") @RequestParam(required = false) String content,
            @ApiParam(value = "Nouveaux fichiers média") @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles,
            @ApiParam(value = "Médias à supprimer") @RequestParam(required = false) List<UUID> mediaToDelete) {

        try {
            // Content moderation check
            if (content != null && !content.isEmpty() && serviceAPI.isContentToxic(content)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "TOXIC_CONTENT"));
            }

            // Process uploaded files
            List<Media> newMediaItems = new ArrayList<>();
            List<String> uploadedPublicIds = new ArrayList<>();

            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                for (MultipartFile file : mediaFiles) {
                    try {
                        // Check image appropriateness
                        if (file.getContentType().contains("image")) {
                            Map tempUploadResult = cloudinary.uploader().upload(
                                    file.getBytes(),
                                    ObjectUtils.asMap("resource_type", "auto", "folder", "temp_moderation")
                            );

                            String tempUrl = (String) tempUploadResult.get("url");
                            String tempPublicId = (String) tempUploadResult.get("public_id");

                            boolean isAppropriate = serviceAPI.isImageAppropriate(tempUrl);
                            cloudinary.uploader().destroy(tempPublicId, ObjectUtils.emptyMap());

                            if (!isAppropriate) {
                                for (String publicId : uploadedPublicIds) {
                                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                                }

                                return ResponseEntity.badRequest()
                                        .body(Map.of("message", "TOXIC_IMAGE"));
                            }
                        }

                        // Upload file
                        Map uploadResult = cloudinary.uploader().upload(
                                file.getBytes(),
                                ObjectUtils.asMap("resource_type", "auto")
                        );

                        String fileUrl = (String) uploadResult.get("url");
                        String publicId = (String) uploadResult.get("public_id");
                        uploadedPublicIds.add(publicId);

                        // Create media entity
                        Media media = new Media();
                        media.setMediaUrl(fileUrl);
                        media.setUserId(postService.getPostById(postId).getUserId());
                        media.setMediaType(file.getContentType().contains("image") ?
                                MediaType.IMAGE : MediaType.VIDEO);

                        newMediaItems.add(media);

                    } catch (Exception e) {
                        // Clean up on error
                        for (String publicId : uploadedPublicIds) {
                            try {
                                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                            } catch (Exception ignored) {}
                        }
                        throw e;
                    }
                }
            }

            Post updatedPost = postService.updatePostWithMedia(postId, content, newMediaItems, mediaToDelete);
            return ResponseEntity.ok(updatedPost);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }



    @GetMapping("/{userId}/media")
    public ResponseEntity<List<Media>> getUserPostsMedia(@PathVariable Integer userId) {
        List<Media> media = postService.getMediaFromUserPosts(userId);
        return ResponseEntity.ok(media);
    }

    @GetMapping("/top-rated-posts")
    public ResponseEntity<List<Map<String, Object>>> getTopRatedPosts() {
        List<Map<String, Object>> topRatedPosts = postService.getTopRatedPostsForCurrentMonth(5);
        return ResponseEntity.ok(topRatedPosts);
    }

    @PostMapping(value = "/image-moderation", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> testImageModeration(
            @RequestParam("image") MultipartFile image) throws IOException {

        // Upload image to get URL
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
        String imageUrl = (String) uploadResult.get("url");

        boolean isAppropriate = serviceAPI.isImageAppropriate(imageUrl);

        return ResponseEntity.ok(Map.of(
                "imageUrl", imageUrl,
                "isAppropriate", isAppropriate,
                "status", isAppropriate ? "The image content is appropriate" : "The image contains inappropriate content"
        ));
    }


    @PostMapping("/generate-image")
    public ResponseEntity<Map<String, String>> generateImage(@RequestBody Map<String, String> request) {
        String description = request.get("description");
        String imageUrl = serviceAPI.generateImage(description);

        Map<String, String> response = new HashMap<>();
        response.put("description", description);
        response.put("imageUrl", imageUrl);

        return ResponseEntity.ok(response);
    }

// @PostMapping("/generate-video")
// @ApiOperation(value = "Generate a video from text description", notes = "Enter a text description to generate a video")
// public ResponseEntity<Map<String, String>> generateVideo(
//         @ApiParam(value = "Description of the video to generate", required = true)
//         @RequestParam String description) {
//
//     String videoUrl = serviceAPI.generateVideo(description);
//
//     Map<String, String> response = new HashMap<>();
//     response.put("description", description);
//     response.put("videoUrl", videoUrl);
//
//     return ResponseEntity.ok(response);
// }

 @PostMapping
@ApiOperation(value = "Generate content or receive webhook callbacks from Predis.ai")
public ResponseEntity<?> webhookHandler(
        HttpServletRequest request,
        @RequestParam(required = false) String description,
        @RequestParam(required = false) String contentType,
        @RequestBody(required = false) Map<String, Object> webhookPayload) {

    // Enhanced webhook handling with detailed logging
    if (webhookPayload != null && webhookPayload.containsKey("post_id")) {
        System.out.println("================== WEBHOOK RECEIVED ==================");
        System.out.println("Webhook payload: " + webhookPayload);

        try {
            String postId = (String) webhookPayload.get("post_id");
            String status = (String) webhookPayload.get("status");
            System.out.println("Post ID: " + postId + ", Status: " + status);

            if ("success".equals(status) || "completed".equals(status)) {
                List<Map<String, Object>> generatedMedia = (List<Map<String, Object>>) webhookPayload.get("generated_media");
                if (generatedMedia != null && !generatedMedia.isEmpty()) {
                    String contentUrl = (String) generatedMedia.get(0).get("url");

                    System.out.println("CONTENT URL FOUND: " + contentUrl);
                    contentTracker.addContentUrl(postId, contentUrl);

                    boolean stored = contentTracker.hasContent(postId);
                    System.out.println("URL stored successfully: " + stored);
                    System.out.println("Stored URL: " + contentTracker.getContentUrl(postId));

                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "message", "Content URL received and stored",
                            "postId", postId,
                            "contentUrl", contentUrl
                    ));
                }
            }

            return ResponseEntity.ok("Webhook processed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("Error processing webhook: " + e.getMessage());
        }
    }
    // Content generation request
    else if (description != null && contentType != null) {
        try {
            String result = "image".equals(contentType)
                ? serviceAPI.generateImage(description)
                : serviceAPI.generateVideo(description);

            // Extract post ID from result string
            String postId = "";
            if (result.contains("ID:")) {
                postId = result.substring(result.indexOf("ID: ") + 4);
                if (postId.contains(" ")) {
                    postId = postId.substring(0, postId.indexOf(" "));
                }
                System.out.println("Extracted Post ID: " + postId);
            }

            Map<String, String> response = new HashMap<>();
            response.put("description", description);
            response.put("contentType", contentType);
            response.put("contentUrl", result);
            response.put("postId", postId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    return ResponseEntity.badRequest().body(Map.of("error", "Invalid request format"));
}

@GetMapping("/content-status/{postId}")
@ApiOperation(value = "Check if generated content is ready")
public ResponseEntity<?> checkContentStatus(@PathVariable String postId) {
    if (contentTracker.hasContent(postId)) {
        return ResponseEntity.ok(Map.of(
            "status", "completed",
            "contentUrl", contentTracker.getContentUrl(postId)
        ));
    } else {
        return ResponseEntity.ok(Map.of(
            "status", "pending",
            "message", "Content is still being generated"
        ));
    }
}



@GetMapping("/{postId}/user-details")
public ResponseEntity<?> getPostWithUserDetails(@PathVariable("postId") UUID postId) {
    try {
        Post post = postService.getPostById(postId);
        UserDtoForum user = userService.getUserDetails(post.getUserId().longValue());

        Map<String, Object> response = new HashMap<>();
        response.put("post", post);
        response.put("user", user);

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", e.getMessage()));
    }
}



    /**
     * Translates text to the specified target language
     * @param requestBody Map containing 'text' to translate and 'targetLang' code
     * @return Translated text
     */
    @PostMapping("/translate")
    public ResponseEntity<Map<String, String>> translateText(@RequestBody Map<String, String> requestBody) {
        String text = requestBody.get("text");
        String targetLang = requestBody.get("targetLang");

        if (text == null || targetLang == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Both 'text' and 'targetLang' fields are required");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String translatedText = serviceAPI.translate(text, targetLang);

        Map<String, String> response = new HashMap<>();
        response.put("originalText", text);
        response.put("translatedText", translatedText);
        response.put("targetLanguage", targetLang);

        return ResponseEntity.ok(response);
    }

    /**
     * Returns all available languages for translation
     * @return Map of language codes to language names
     */
    @GetMapping("/languages")
    public ResponseEntity<Map<String, String>> getAvailableLanguages() {
        return ResponseEntity.ok(serviceAPI.getAvailableLanguages());
    }


@PostMapping("/analyze")
public ResponseEntity<String> analyzeText(@RequestBody String textRequest) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

    Map<String, String> requestMap = new HashMap<>();
    requestMap.put("text", textRequest);

    // Convert map to JSON
    ObjectMapper mapper = new ObjectMapper();
    String jsonRequest;
    try {
        jsonRequest = mapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing request: " + e.getMessage());
    }

    HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

    ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/predict", entity, String.class);

    return ResponseEntity.ok(response.getBody());
}



}