package tn.esprit.spring.forumservice.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import tn.esprit.spring.forumservice.Service.Interfaces.CommentService;
import tn.esprit.spring.forumservice.entity.Comment;

import java.util.UUID;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<Comment> addCommentToPost(
            @PathVariable UUID postId,
            @RequestParam("content") String content,
            @RequestParam(value = "userId", required = false) Integer userId) {

        Comment comment = commentService.addComment(postId, content, userId);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }
}
