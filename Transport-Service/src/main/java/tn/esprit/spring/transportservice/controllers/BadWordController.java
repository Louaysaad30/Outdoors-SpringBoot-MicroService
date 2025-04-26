package tn.esprit.spring.transportservice.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.spring.transportservice.entity.BadWord;
import tn.esprit.spring.transportservice.repository.BadWordRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class BadWordController {

    @Autowired
    private BadWordRepository badWordRepository;

    @GetMapping("/bad-words")
    public List<String> getBadWords() {
        return badWordRepository.findAll()
                .stream()
                .map(BadWord::getWord)
                .collect(Collectors.toList());
    }
}