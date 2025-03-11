package tn.esprit.spring.forumservice.Controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.forumservice.Service.Interfaces.MediaService;
import tn.esprit.spring.forumservice.Service.Interfaces.PostService;
import tn.esprit.spring.forumservice.entity.Media;
import tn.esprit.spring.forumservice.entity.MediaType;
import tn.esprit.spring.forumservice.entity.Post;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final MediaService mediaService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<Post> createPost(
            @RequestParam("content") String content,
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles) throws IOException {

        Post post = new Post();
        post.setContent(content);
        post.setUserId(userId);
        post.setCreatedAt(java.time.LocalDateTime.now());

        // Save the post first to avoid TransientPropertyValueException
        Post savedPost = postService.createPost(post);

        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            List<Media> mediaList = new ArrayList<>();
            for (MultipartFile file : mediaFiles) {
                String fileName = saveMediaFile(file);

                Media media = new Media();
                media.setMediaUrl(fileName);
                media.setMediaType(file.getContentType().contains("image") ? MediaType.IMAGE : MediaType.VIDEO);
                media.setPost(savedPost); // Now using the saved post

                mediaService.saveMedia(media);
                mediaList.add(media);
            }

            savedPost.setMedia(mediaList);
            savedPost.setHasMedia(true);
            // Update the post with media information
            savedPost = postService.createPost(savedPost);
        } else {
            savedPost.setHasMedia(false);
        }

        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }


    private String saveMediaFile(MultipartFile file) throws IOException {
        // Create a proper File object to handle the path correctly
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destFile = new File(directory, fileName);
        file.transferTo(destFile);

        return fileName;
    }

    @GetMapping("/user/{userId}")
    public List<Post> getUserPosts(@PathVariable Integer userId) {
        return postService.getPostsByUser(userId);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostDetails(@PathVariable("postId") UUID postId) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build(); // Retourne 404 si le post n'existe pas
        }
        return ResponseEntity.ok(post); // Retourne le post avec un statut 200
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build(); // Retourne 204 si le post est supprimé
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
