package tn.esprit.spring.forumservice.Controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.ws.rs.GET;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.forumservice.Service.API.ServiceAPI;
import tn.esprit.spring.forumservice.Service.Interfaces.MediaService;
import tn.esprit.spring.forumservice.Service.Interfaces.PostService;
import tn.esprit.spring.forumservice.entity.Media;
import tn.esprit.spring.forumservice.entity.MediaType;
import tn.esprit.spring.forumservice.entity.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final PostService postService;
    private final MediaService mediaService;
    private final Cloudinary cloudinary;
    private final ServiceAPI serviceAPI;

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

    @ApiOperation(value = "Mettre à jour un post", notes = "Cette méthode permet de mettre à jour le contenu d'un post et d'ajouter de nouveaux médias.")
    @PutMapping("/{postId}")
    public Post updatePost(
            @ApiParam(value = "ID du post à mettre à jour", required = true) @PathVariable UUID postId,
            @ApiParam(value = "Nouveau contenu du post") @RequestParam(required = false) String content,
            @ApiParam(value = "URLs des nouveaux médias") @RequestParam(required = false) List<String> mediaUrls,
            @ApiParam(value = "Types des nouveaux médias") @RequestParam(required = false) List<String> mediaTypes,
            @ApiParam(value = "Médias à supprimer") @RequestParam(required = false) List<UUID> mediaToDelete) {

        return postService.updatePost(postId, content, mediaUrls, mediaTypes, mediaToDelete);
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
}