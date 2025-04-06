package tn.esprit.spring.userservice.Service.IMPL;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.userservice.Entity.Token;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Enum.EmailTemplateName;
import tn.esprit.spring.userservice.Enum.RoleType;
import tn.esprit.spring.userservice.Repository.RoleRepository;
import tn.esprit.spring.userservice.Repository.TokenRepository;
import tn.esprit.spring.userservice.Repository.UserRepository;
import tn.esprit.spring.userservice.Security.JwtService;
import tn.esprit.spring.userservice.Service.Interface.AuthenticationService;
import tn.esprit.spring.userservice.Service.Interface.EmailService;
import tn.esprit.spring.userservice.dto.Request.AuthenticationRequest;
import tn.esprit.spring.userservice.dto.Request.RegistrationRequest;
import tn.esprit.spring.userservice.dto.Response.AuthenticationResponse;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor

public class AuthentificationServiceImpl implements AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private  final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private  final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    @Value("${application.mailing.frontend.activation-url:http://localhost:4200/activate-account}")
    private String activationUrl;


    @Override
    public void register(RegistrationRequest request) throws MessagingException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email '" + request.getEmail() + "' is already in use.");
        }

        var userRole = roleRepository.findByRoleType(RoleType.USER)
                .orElseThrow(() -> new IllegalArgumentException("Role user is not initialized"));

        User user = new User();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setDateNaissance(request.getDateNaissance());
        user.setTel(request.getTel());
        user.setImage(request.getImage());
        user.setMotDePasse(bCryptPasswordEncoder.encode(request.getMotDePasse()));
        user.setEmail(request.getEmail());
        user.setAccountLocked(false);
        user.setEnabled(false);
        user.setRoles(List.of(userRole));

        userRepository.save(user);
        sendValidationEmail(user);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Check if the email exists in the database
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password"));

        // Check if the account is locked
        if (user.isAccountLocked()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is locked");
        }

        // Check if the account is enabled (activated)
        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not activated. Please check your email.");
        }

        // Check if the password matches
        if (!bCryptPasswordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password");
        }

        // Generate JWT token if authentication passes
        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.fullName());

        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    @Override
    //@Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken =tokenRepository.findByToken(token)
                .orElseThrow(()->new RuntimeException("invalid token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("activation token has expired a new token has been send to same email addresse");
        }
        User user= userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(()->new RuntimeException("user not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
    @Override
    public void resendToken(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));

        if (user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is already activated.");
        }

        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken=generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        //generate token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(120))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }
    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
}