package com.hrplatform.backend.model.dto.auth;

public record AuthResponse(
        String token,
        Long userId,
        Long companyId,
        String email,
        String role
) {}
