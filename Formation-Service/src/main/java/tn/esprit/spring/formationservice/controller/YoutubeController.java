package tn.esprit.spring.formationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tn.esprit.spring.formationservice.services.IMPL.YoutubeServiceImpl;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YoutubeController {

    private final YoutubeServiceImpl youtubeService;

    @GetMapping("/video")
    public Mono<String> getVideoId(@RequestParam("query") String query) {
        return youtubeService.getVideoId(query);
    }
}
