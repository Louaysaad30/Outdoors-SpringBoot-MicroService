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

@GetMapping("/all")
public ResponseEntity<List<Post>> getAllPosts() {
    List<Post> posts = postService.getAllPosts();
    return ResponseEntity.ok(posts);
}

@PostMapping(value = "/add", consumes = "multipart/form-data")
public ResponseEntity<Post> createPost(@RequestParam("content") String content, @RequestParam("userId") Integer userId, @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles) {

        try {
            Post post = new Post();
            post.setContent(content);
            post.setUserId(userId);
            post.setCreatedAt(java.time.LocalDateTime.now());

            // Save the post first to avoid TransientPropertyValueException
            Post savedPost = postService.createPost(post);

            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                List<Media> mediaList = new ArrayList<>();
                for (MultipartFile file : mediaFiles) {
                    Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                    String fileUrl = (String) uploadResult.get("url");

                    Media media = new Media();
                    media.setMediaUrl(fileUrl);
                    media.setMediaType(file.getContentType().contains("image") ? MediaType.IMAGE : MediaType.VIDEO);
                    media.setPost(savedPost);

                    mediaService.saveMedia(media);
                    mediaList.add(media);
                }

                savedPost.setMedia(mediaList);
                savedPost.setHasMedia(true);
                savedPost = postService.createPost(savedPost);
            } else {
                savedPost.setHasMedia(false);
            }

            return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
}