package com.doubleo.didagent.dto.response.poll;

import jakarta.validation.constraints.NotBlank;

public record InvitationInfoResponse(@NotBlank String invitationUrl) {}
