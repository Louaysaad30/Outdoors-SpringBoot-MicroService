package tn.esprit.spring.formationservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.formationservice.entity.Pause;
import tn.esprit.spring.formationservice.repository.PauseRepository;
import tn.esprit.spring.formationservice.services.interfaces.IPauseService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PauseServiceImpl implements IPauseService {

    private final PauseRepository pauseRepository;

    @Override
    public Pause addPause(Pause pause) {
        return pauseRepository.save(pause);
    }

    @Override
    public List<Pause> getPausesByFormation(Long formationId) {
        return pauseRepository.findByFormationId(formationId);
    }
}
