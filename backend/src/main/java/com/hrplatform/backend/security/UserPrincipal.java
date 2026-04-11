package com.hrplatform.backend.security;

import java.io.Serializable;

public record UserPrincipal(
        Long userId,
        Long companyId,
        String email,
        String role
) implements Serializable {}
