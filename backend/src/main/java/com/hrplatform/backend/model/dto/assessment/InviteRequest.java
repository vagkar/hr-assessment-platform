package com.hrplatform.backend.model.dto.assessment;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteRequest(
        @NotBlank String candidateName,
        @NotBlank @Email String candidateEmail
) {}
