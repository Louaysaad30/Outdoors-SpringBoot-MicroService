package tn.esprit.spring.forumservice.Service.IMPL;

           import lombok.RequiredArgsConstructor;
           import org.springframework.stereotype.Service;
           import tn.esprit.spring.forumservice.Repository.PostRepository;
           import tn.esprit.spring.forumservice.Service.Interfaces.MediaService;
           import tn.esprit.spring.forumservice.Service.Interfaces.PostService;
           import tn.esprit.spring.forumservice.entity.Media;
           import tn.esprit.spring.forumservice.entity.MediaType;
           import tn.esprit.spring.forumservice.entity.Post;

           import java.util.List;
           import java.util.Optional;
           import java.util.UUID;

           @Service
           @RequiredArgsConstructor
           public class PostServiceIMPL implements PostService {

               private final PostRepository postRepository;
               private final MediaService mediaService;

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

               @Override
               public List<Post> getPostsByUser(Integer userId) {
                   return postRepository.findByUserIdWithMedia(userId);
               }

               @Override
               public Post updatePost(UUID postId, String content, List<String> mediaUrls, List<String> mediaTypes, List<UUID> mediaToDelete) {
                   Optional<Post> postOptional = postRepository.findById(postId);
                   if (postOptional.isPresent()) {
                       Post post = postOptional.get();

                       // Mettre à jour le texte du post
                       if (content != null) {
                           post.setContent(content);
                       }

                       // Ajouter de nouveaux médias
                       if (mediaUrls != null && !mediaUrls.isEmpty()) {
                           for (int i = 0; i < mediaUrls.size(); i++) {
                               Media media = Media.builder()
                                       .mediaUrl(mediaUrls.get(i))
                                       .mediaType(MediaType.valueOf(mediaTypes.get(i).toUpperCase()))  // Convert String to MediaType enum
                                       .post(post)
                                       .build();
                               mediaService.saveMedia(media);  // Utiliser le service pour enregistrer le média
                               post.getMedia().add(media); // Ajouter le média à la liste des médias du post
                           }
                       }

                       // Supprimer les anciens médias
                       if (mediaToDelete != null && !mediaToDelete.isEmpty()) {
                           for (UUID mediaId : mediaToDelete) {
                               Optional<Media> mediaOptional = post.getMedia().stream()
                                       .filter(media -> media.getId().equals(mediaId))
                                       .findFirst();

                               mediaOptional.ifPresent(media -> {
                                   post.getMedia().remove(media);
                                   mediaService.deleteMedia(media);  // Utiliser le service pour supprimer le média
                               });
                           }
                       }

                       // Sauvegarder les changements du post
                       postRepository.save(post);
                       return post;  // Return the updated post
                   }
                   return null;  // Return null if not found
               }

           }