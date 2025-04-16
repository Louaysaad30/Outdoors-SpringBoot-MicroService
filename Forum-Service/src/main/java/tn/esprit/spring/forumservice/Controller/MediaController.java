package tn.esprit.spring.forumservice.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.spring.forumservice.Service.Interfaces.MediaService;
import tn.esprit.spring.forumservice.entity.Media;

import java.util.List;

@RestController
@RequestMapping("/media")

public class MediaController {
    @Autowired
    private MediaService mediaService;


    @GetMapping("/{userId}")
    public ResponseEntity<List<Media>> getMediaByUserId(@PathVariable Integer userId) {
        List<Media> userMedia = mediaService.getMediaByUserId(userId);
        return ResponseEntity.ok(userMedia);
    }

}
