package tn.esprit.spring.forumservice.Service.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.forumservice.Repository.CommentRepository;
import tn.esprit.spring.forumservice.Repository.PostRepository;
import tn.esprit.spring.forumservice.Service.Interfaces.CommentService;
import tn.esprit.spring.forumservice.entity.Comment;
import tn.esprit.spring.forumservice.entity.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceIMPL implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public Comment addComment(UUID postId, String content, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Comment comment = Comment.builder()
                .content(content)
                .userId(userId)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public Comment getCommentById(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
    }

    @Override
    public List<Comment> getCommentsByPostId(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // This would be more efficient with a custom query in CommentRepository
        return post.getComments();
    }

    @Override
    public Comment updateComment(UUID id, String content) {
        Comment comment = getCommentById(id);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(UUID id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment replyToComment(UUID parentCommentId, String content, Integer userId) {
        Comment parentComment = getCommentById(parentCommentId);
        Post post = parentComment.getPost();

        Comment reply = Comment.builder()
                .content(content)
                .userId(userId)
                .post(post)
                .parentComment(parentComment)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(reply);
    }
}