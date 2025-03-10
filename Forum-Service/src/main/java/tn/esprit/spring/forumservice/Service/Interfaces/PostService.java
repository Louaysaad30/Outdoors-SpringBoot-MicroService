package tn.esprit.spring.forumservice.Service.Interfaces;

import tn.esprit.spring.forumservice.entity.Post;

import java.util.List;
import java.util.UUID;

public interface PostService {
    Post createPost(Post post);
    Post getPostById(UUID id);
    List<Post> getAllPosts();
    Post updatePost(UUID id, Post post);
    void deletePost(UUID id);
}
