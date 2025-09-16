package edu.auca.lms.auth;

import edu.auca.lms.security.JwtService;
import edu.auca.lms.user.Role;
import edu.auca.lms.user.User;
import edu.auca.lms.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(Map.of(
                "role", user.getRole().name(),
                "name", user.getFullName()
        ), request.getUsername());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                        "username", user.getUsername(),
                        "fullName", user.getFullName(),
                        "email", user.getEmail(),
                        "role", user.getRole().name()
                )
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }
        Role role = request.getRole() == null ? Role.STUDENT : request.getRole();
        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(role)
                .enabled(true)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        return userRepository.findByUsername(authentication.getName())
                .map(u -> ResponseEntity.ok(Map.of(
                        "username", u.getUsername(),
                        "fullName", u.getFullName(),
                        "email", u.getEmail(),
                        "role", u.getRole().name()
                )))
                .orElse(ResponseEntity.status(404).build());
    }

    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        @NotBlank
        private String fullName;
        @NotBlank
        private String email;
        private Role role;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
    }
}

