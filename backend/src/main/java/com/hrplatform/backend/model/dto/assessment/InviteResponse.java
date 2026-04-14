package com.hrplatform.backend.model.dto.assessment;

public record InviteResponse(
        String inviteToken,
        String inviteUrl
) {}
