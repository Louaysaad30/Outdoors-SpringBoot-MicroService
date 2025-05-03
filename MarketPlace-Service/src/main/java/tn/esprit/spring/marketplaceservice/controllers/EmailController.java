package tn.esprit.spring.marketplaceservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.spring.marketplaceservice.services.IMPL.EmailService;

import java.util.Map;

@RestController
@RequestMapping("/api/mail")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody Map<String, String> payload) {
        String to = payload.get("to");
        String subject = payload.get("subject");
        String body = payload.get("body");
        String contentType = payload.get("contentType"); // Get content type from payload

        boolean isHtml = contentType != null && contentType.equals("text/html");

        emailService.sendEmail(to, subject, body, isHtml); // Pass isHtml flag
        return ResponseEntity.ok("Email envoyé avec succès !");
    }
}

