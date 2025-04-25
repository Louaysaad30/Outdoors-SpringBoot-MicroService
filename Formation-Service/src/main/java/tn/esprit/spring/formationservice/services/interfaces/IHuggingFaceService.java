package tn.esprit.spring.formationservice.services.interfaces;

import java.util.List;

public interface IHuggingFaceService {
    String generateBetterDescription(String rawText);
    float[] computeSimilarity(String source, List<String> sentences);
}
