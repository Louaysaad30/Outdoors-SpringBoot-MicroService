package tn.esprit.spring.transportservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import tn.esprit.spring.transportservice.entity.BadWord;
import tn.esprit.spring.transportservice.repository.BadWordRepository;

import java.util.List;

@EnableDiscoveryClient
@SpringBootApplication
public class TransportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransportServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initBadWords(BadWordRepository repo) {
        return args -> {
            List<String> defaultWords = List.of(
                    "merde", "con", "putain", "shit", "fuck",
                    "asshole", "bitch", "damn", "hell"
            );

            defaultWords.forEach(word -> {
                if (!repo.existsByWord(word)) {
                    BadWord badWord = new BadWord();
                    badWord.setWord(word);
                    repo.save(badWord);
                }
            });
        };
    }
}
