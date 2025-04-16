package tn.esprit.spring.eventservice.services.interfaces;

public interface IHuggingFaceService {
    String improveText(String originalText);
    String[] extractKeywords(String text);
    boolean didLastCallUseFallback();
    byte[] generateImage(String prompt);

}