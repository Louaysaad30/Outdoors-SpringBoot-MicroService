package tn.esprit.spring.userservice.Controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Service.Interface.AuthenticationService;
import tn.esprit.spring.userservice.dto.Request.AuthenticationRequest;
import tn.esprit.spring.userservice.dto.Request.RegistrationRequest;
import tn.esprit.spring.userservice.dto.Response.AuthenticationResponse;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {
    @Qualifier("authentificationServiceImpl")
    private  final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        try {
            service.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "User registered successfully"));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("status", ex.getStatusCode().value(), "message", ex.getReason()));
        }
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        try {
            AuthenticationResponse response = service.authenticate(request);
            return ResponseEntity.ok(response); // Return the JWT token if authentication is successful
        } catch (ResponseStatusException ex) {
            // Handle different exception cases for more fine-grained error responses
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("status", ex.getStatusCode().value(), "message", ex.getReason()));
        } catch (RuntimeException e) {
            // For unexpected errors, you can return a generic error message
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    @GetMapping("/activate-account")
    public void confirm(@RequestParam String token) throws MessagingException {
        service.activateAccount(token);
    }
    @PostMapping("/resend-token")
    public ResponseEntity<?> resendActivationToken(@RequestParam String email) {
        try {
            service.resendToken(email);
            return ResponseEntity.ok(Collections.singletonMap("message", "Activation email has been resent."));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("status", ex.getStatusCode().value(), "message", ex.getReason()));
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to send activation email."));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // No real logic needed with JWT, just return OK
        return ResponseEntity.ok("User logged out successfully.");
    }

}