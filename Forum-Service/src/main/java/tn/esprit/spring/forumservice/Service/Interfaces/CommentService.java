package tn.esprit.spring.forumservice.Service.Interfaces;

import tn.esprit.spring.forumservice.entity.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    Comment addComment(UUID postId, String content, Integer userId);
    Comment getCommentById(UUID id);
    List<Comment> getCommentsByPostId(UUID postId);
    Comment updateComment(UUID id, String content);
    void deleteComment(UUID id);
    Comment replyToComment(UUID parentCommentId, String content, Integer userId);
}

