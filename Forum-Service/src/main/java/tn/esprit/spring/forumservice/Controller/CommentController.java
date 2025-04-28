package tn.esprit.spring.forumservice.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import tn.esprit.spring.forumservice.Service.IMPL.UserService;
import tn.esprit.spring.forumservice.Service.Interfaces.CommentService;
import tn.esprit.spring.forumservice.entity.Comment;
import tn.esprit.spring.forumservice.entity.Post;
import tn.esprit.spring.forumservice.entity.UserDtoForum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @PostMapping("/{postId}")
    public ResponseEntity<?> addCommentToPost(
            @PathVariable UUID postId,
            @RequestParam("content") String content,
            @RequestParam(value = "userId", required = false) Integer userId) {

        try {
            Comment comment = commentService.addComment(postId, content, userId);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("inappropriate language")) {
                return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
            } else if (e.getMessage().contains("Post not found")) {
                return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(Map.of("error", "An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> editComment(
            @PathVariable UUID commentId,
            @RequestParam("content") String content) {

        Comment updatedComment = commentService.updateComment(commentId, content);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reply/{commentId}")
    public ResponseEntity<?> replyToComment(
            @PathVariable UUID commentId,
            @RequestParam("content") String content,
            @RequestParam(value = "userId", required = false) Integer userId) {

        try {
            Comment reply = commentService.replyToComment(commentId, content, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(reply);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("inappropriate language")) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .body(Map.of("error", "Reply contains inappropriate language"));
            } else if (e.getMessage().contains("Comment not found")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

@GetMapping("/top-level/{postId}")
public ResponseEntity<List<Comment>> getTopLevelComments(@PathVariable UUID postId) {
    List<Comment> topLevelComments = commentService.getTopLevelComments(postId);
    return ResponseEntity.ok(topLevelComments);
}

@PostMapping("/{commentId}/user-details")
    public ResponseEntity<?> getCommentWithUserDetails(@PathVariable("commentId") UUID commentId) {
        try {
            Comment comment = commentService.getCommentById(commentId);
            UserDtoForum user = userService.getUserDetails(comment.getUserId().longValue());

            Map<String, Object> response = new HashMap<>();
            response.put("comment", comment);
            response.put("user", user);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }



}
