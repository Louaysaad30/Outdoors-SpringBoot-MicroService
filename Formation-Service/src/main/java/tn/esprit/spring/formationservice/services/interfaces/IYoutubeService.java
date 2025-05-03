package tn.esprit.spring.formationservice.services.interfaces;

import reactor.core.publisher.Mono;

public interface IYoutubeService {
    Mono<String> getVideoId(String query);
}
