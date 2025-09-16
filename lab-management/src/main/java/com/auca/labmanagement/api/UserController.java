package com.auca.labmanagement.api;

import com.auca.labmanagement.api.dto.UserDtos.CreateUserRequest;
import com.auca.labmanagement.api.dto.UserDtos.UpdateUserRequest;
import com.auca.labmanagement.api.dto.UserDtos.UserResponse;
import com.auca.labmanagement.domain.User;
import com.auca.labmanagement.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> list() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest req) {
        if (userRepository.existsByUsername(req.username()) || userRepository.existsByEmail(req.email())) {
            return ResponseEntity.unprocessableEntity().build();
        }
        var user = User.builder()
                .username(req.username())
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .roles(req.roles())
                .enabled(req.enabled())
                .build();
        user = userRepository.save(user);
        return ResponseEntity.created(URI.create("/api/admin/users/" + user.getId())).body(toResponse(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        return userRepository.findById(id).map(u -> ResponseEntity.ok(toResponse(u))).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();
        var user = userOpt.get();
        if (req.email() != null) user.setEmail(req.email());
        if (req.password() != null) user.setPasswordHash(passwordEncoder.encode(req.password()));
        if (req.roles() != null) user.setRoles(req.roles());
        if (req.enabled() != null) user.setEnabled(req.enabled());
        user = userRepository.save(user);
        return ResponseEntity.ok(toResponse(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRoles(), u.isEnabled());
    }
}

