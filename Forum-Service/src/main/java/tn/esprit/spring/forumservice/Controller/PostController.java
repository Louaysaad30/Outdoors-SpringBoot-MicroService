package tn.esprit.spring.forumservice.Controller;

    import lombok.AllArgsConstructor;
    import lombok.RequiredArgsConstructor;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import tn.esprit.spring.forumservice.Service.Interfaces.PostService;
    import tn.esprit.spring.forumservice.entity.Post;

    @RestController
    @RequestMapping("/post")
    @RequiredArgsConstructor
    public class PostController {

        private final PostService postService;

        @PostMapping("/add")
        public Post createPost(@RequestBody Post post) {
            return postService.createPost(post);
        }

    }