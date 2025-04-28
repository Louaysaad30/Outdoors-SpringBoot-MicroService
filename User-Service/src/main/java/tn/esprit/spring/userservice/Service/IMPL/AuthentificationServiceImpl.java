package tn.esprit.spring.userservice.Service.IMPL;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.userservice.Entity.Token;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Entity.UserDetail;
import tn.esprit.spring.userservice.Enum.EmailTemplateName;
import tn.esprit.spring.userservice.Enum.RoleType;
import tn.esprit.spring.userservice.Repository.RoleRepository;
import tn.esprit.spring.userservice.Repository.TokenRepository;
import tn.esprit.spring.userservice.Repository.UserDetailRepository;
import tn.esprit.spring.userservice.Repository.UserRepository;
import tn.esprit.spring.userservice.Security.JwtService;
import tn.esprit.spring.userservice.Service.Interface.AuthenticationService;
import tn.esprit.spring.userservice.Service.Interface.EmailService;
import tn.esprit.spring.userservice.Service.Interface.ICloudinaryService;
import tn.esprit.spring.userservice.Service.Interface.UserService;
import tn.esprit.spring.userservice.dto.Request.AuthenticationRequest;
import tn.esprit.spring.userservice.dto.Request.RegistrationRequest;
import tn.esprit.spring.userservice.dto.Response.AuthenticationResponse;
import tn.esprit.spring.userservice.dto.Response.UserDetailDTO;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class AuthentificationServiceImpl implements AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private  final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserDetailRepository userDetailRepository;

    private  final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    @Value("${application.mailing.frontend.activation-url:http://localhost:4200/activate-account}")
    private String activationUrl;
    private final ICloudinaryService cloudinaryService;



    @Override
    public void register(RegistrationRequest request) throws MessagingException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email '" + request.getEmail() + "' is already in use.");
        }

        RoleType roleType = RoleType.valueOf(request.getRole()); // e.g., AGENT or USER
        var userRole = roleRepository.findByRoleType(roleType)
                .orElseThrow(() -> new IllegalArgumentException("Role " + roleType + " is not initialized"));

        String imageUrl = null;
        MultipartFile image = request.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = cloudinaryService.uploadImage(image);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Image upload failed", e);
            }
        }

        User user = new User();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setDateNaissance(request.getDateNaissance());
        user.setTel(request.getTel());
        user.setImage(imageUrl);
        user.setMotDePasse(bCryptPasswordEncoder.encode(request.getMotDePasse()));
        user.setEmail(request.getEmail());
        user.setAccountLocked(false);
        user.setEnabled(false);
        user.setStatus(false);

        user.setRoles(List.of(userRole));

        // Only for AGENT role
        if (roleType == RoleType.AGENCE) {
            user.setLocation(request.getLocation());
        }

        userRepository.save(user);
        // Create and save the UserDetail entry
        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user); // Set the user ID
        userDetail.setSessions(0);
        userDetail.setTotalSessions(0);
        userDetail.setNDaysAfterOnboarding(0);
        userDetail.setTotalNavigationsFav1(0);
        userDetail.setActivityDays(0);
        userDetailRepository.save(userDetail);
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

        // ✅ Check if status is true (only allow login if status is true)
        if (!user.getStatus()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account is under verification .");
        }

        // Check if the password matches
        if (!bCryptPasswordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password");
        }

        // Generate JWT token if authentication passes
        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.fullName());
        try {
            userService.incrementSessionStats(user.getId());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error incrementing session stats", e);
        }

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

    @Override
    public boolean verifyPassword(Long id, String enteredPassword) {
        User user = userRepository.findById(id).orElse(null);
        // Assuming you're using BCrypt or any other PasswordEncoder to compare the hashed passwords
        return bCryptPasswordEncoder.matches(enteredPassword, user.getPassword());
    }
    public void resetPassword(String token, String newPassword) {
        Token resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        User user = resetToken.getUser();
        // Validate that the password is new (not the same as the old one)
        if (bCryptPasswordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("New password cannot be the same as the old one");
        }


        // Update the password (using a password encoder, for example)
        user.setMotDePasse(bCryptPasswordEncoder.encode(newPassword)); // Fixed line
        userRepository.save(user);

        // Optionally, you can mark the token as validated
        resetToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);
    }

    public void sendResetLink(String email) throws MessagingException {
        // Fetch the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate a reset token
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusHours(1);  // Set expiry time for the token
        var resetToken = Token.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiryTime)
                .user(user)
                .build();
        // Create and save the token in the database
        tokenRepository.save(resetToken);

        // Construct the password reset URL
        String resetUrl = "http://localhost:4200/auth/pass-change?token=" + token;

        // Send the reset link via email
        emailService.sendEmail(user.getEmail(), user.fullName(), EmailTemplateName.valueOf("RESET_PASSWORD"), resetUrl, token, "Password Reset Request");
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
        System.out.println("activation code: "+ codeBuilder.toString());

        return codeBuilder.toString();
    }
    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }

        user.setMotDePasse(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    @Override
    public boolean verifyRecaptcha(String token) {
        String secret = "6LewnB4rAAAAAESdvbw1XNwOfsVHeuJmZl2lye5W"; // récupérée depuis l'admin Google reCAPTCHA
        String url = "https://www.google.com/recaptcha/api/siteverify";

        try {
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secret);
            params.add("response", token);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map body = response.getBody();
            return body != null && Boolean.TRUE.equals(body.get("success"));
        } catch (Exception e) {
            return false;
        }
    }


}