package tn.esprit.spring.forumservice.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.forumservice.Service.Interfaces.ReactionService;
import tn.esprit.spring.forumservice.entity.Reaction;
import tn.esprit.spring.forumservice.entity.ReactionType;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reaction")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<Reaction> addReaction(
            @PathVariable UUID postId,
            @RequestParam("userId") Integer userId,
            @RequestParam("reactionType") ReactionType reactionType) {

        Reaction reaction = reactionService.addReaction(postId, userId, reactionType);
        return new ResponseEntity<>(reaction, HttpStatus.CREATED);
    }

    @DeleteMapping("/{reactionId}")
    public ResponseEntity<Void> deleteReaction(@PathVariable UUID reactionId) {
        reactionService.deleteReaction(reactionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{reactionId}")
    public ResponseEntity<Reaction> updateReaction(
            @PathVariable UUID reactionId,
            @RequestParam("reactionType") ReactionType newReactionType) {

        Reaction reaction = reactionService.updateReaction(reactionId, newReactionType);
        return ResponseEntity.ok(reaction);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Reaction>> getReactionsByPostId(@PathVariable UUID postId) {
        List<Reaction> reactions = reactionService.getReactionsByPostId(postId);
        return ResponseEntity.ok(reactions);
    }

    @GetMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<Reaction> getUserReactionOnPost(
            @PathVariable UUID postId,
            @PathVariable Integer userId) {

        Reaction reaction = reactionService.getUserReactionOnPost(postId, userId);
        if (reaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reaction);
    }

    @DeleteMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<Void> deleteUserReactionOnPost(
            @PathVariable UUID postId,
            @PathVariable Integer userId) {

        Reaction reaction = reactionService.getUserReactionOnPost(postId, userId);
        if (reaction != null) {
            reactionService.deleteReaction(reaction.getId());
        }
        return ResponseEntity.noContent().build();
    }
}