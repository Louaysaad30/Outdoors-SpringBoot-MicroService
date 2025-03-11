package tn.esprit.spring.forumservice.Service.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.forumservice.Repository.MediaRepository;
import tn.esprit.spring.forumservice.Service.Interfaces.MediaService;
import tn.esprit.spring.forumservice.entity.Media;

@Service
@RequiredArgsConstructor
public class MediaServiceIMPL implements MediaService {
    private final MediaRepository mediaRepository;

    @Override
    public void saveMedia(Media media) {
        mediaRepository.save(media);
    }
    @Override
    public void deleteMedia(Media media) {
        mediaRepository.delete(media);
    }
}
