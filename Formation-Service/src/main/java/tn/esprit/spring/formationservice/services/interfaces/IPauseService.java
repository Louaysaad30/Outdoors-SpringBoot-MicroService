package tn.esprit.spring.formationservice.services.interfaces;

import tn.esprit.spring.formationservice.entity.Pause;

import java.util.List;

public interface IPauseService {
    Pause addPause(Pause pause);
    List<Pause> getPausesByFormation(Long formationId);
}
