package com.auca.labmanagement.api.dto;

import com.auca.labmanagement.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class UserDtos {
    public record CreateUserRequest(
            @NotBlank @Size(max = 100) String username,
            @NotBlank @Email @Size(max = 200) String email,
            @NotBlank @Size(min = 6, max = 120) String password,
            @NotNull Set<UserRole> roles,
            boolean enabled
    ) {}

    public record UpdateUserRequest(
            @Email @Size(max = 200) String email,
            @Size(min = 6, max = 120) String password,
            Set<UserRole> roles,
            Boolean enabled
    ) {}

    public record UserResponse(Long id, String username, String email, Set<UserRole> roles, boolean enabled) {}
}

