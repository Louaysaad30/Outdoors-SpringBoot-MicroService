package tn.esprit.spring.forumservice.Service.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.forumservice.Repository.PostRepository;
import tn.esprit.spring.forumservice.Service.Interfaces.PostService;
import tn.esprit.spring.forumservice.entity.Post;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PostServiceIMPL implements PostService {

    private final PostRepository postRepository;
    @Override
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post getPostById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post updatePost(UUID id, Post post) {
        Post existingPost = getPostById(id);
        existingPost.setContent(post.getContent());
        existingPost.setHasMedia(post.getHasMedia());
        return postRepository.save(existingPost);
    }

    @Override
    public void deletePost(UUID id) {
        postRepository.deleteById(id);
    }
}
