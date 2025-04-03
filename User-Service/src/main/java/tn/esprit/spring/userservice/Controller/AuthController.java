package tn.esprit.spring.userservice.Controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.userservice.Service.Interface.AuthenticationService;
import tn.esprit.spring.userservice.dto.Request.AuthenticationRequest;
import tn.esprit.spring.userservice.dto.Request.RegistrationRequest;
import tn.esprit.spring.userservice.dto.Response.AuthenticationResponse;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
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
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    @GetMapping("/activate-account")
    public void confirm(@RequestParam String token) throws MessagingException {
        service.activateAccount(token);
    }

}