package tn.esprit.spring.userservice.Service.Interface;

import jakarta.mail.MessagingException;
import tn.esprit.spring.userservice.Enum.EmailTemplateName;

public interface EmailService {
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject
    )throws MessagingException;
}