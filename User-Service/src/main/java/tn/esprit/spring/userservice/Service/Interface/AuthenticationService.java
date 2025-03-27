package tn.esprit.spring.userservice.Service.Interface;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.dto.Request.RegistrationRequest;

@Service
public interface AuthenticationService {
    public void register(RegistrationRequest request) throws MessagingException;

}
