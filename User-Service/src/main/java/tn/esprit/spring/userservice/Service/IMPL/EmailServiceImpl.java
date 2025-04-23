package tn.esprit.spring.userservice.Service.IMPL;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.context.Context;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tn.esprit.spring.userservice.Enum.EmailTemplateName;
import tn.esprit.spring.userservice.Service.Interface.EmailService;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@AllArgsConstructor

public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmail(String to, String username, EmailTemplateName emailTemplate, String confirmationUrl, String activationCode, String subject)
            throws MessagingException {
        String templateName;
        if (emailTemplate == null) {
            templateName = "confirm-email";
        } else {
            templateName = emailTemplate.name();
        }
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

        Context context = new Context();
        context.setVariables(properties);


        helper.setFrom("onsfidha3@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);

        helper.setText(template, true);

        mailSender.send(mimeMessage);
    }

}