package tn.esprit.spring.userservice.Service.Interface;

import jakarta.mail.MessagingException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.dto.Request.AuthenticationRequest;
import tn.esprit.spring.userservice.dto.Request.RegistrationRequest;
import tn.esprit.spring.userservice.dto.Response.AuthenticationResponse;

@Service
public interface AuthenticationService {
    public void register(RegistrationRequest request) throws  MessagingException;

    public AuthenticationResponse authenticate(AuthenticationRequest request);

    void activateAccount(String token) throws MessagingException;
    public void resendToken(String email) throws MessagingException;
    public boolean verifyPassword(Long id, String enteredPassword);
}
