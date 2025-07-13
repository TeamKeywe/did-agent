package com.doubleo.didagent.dto.request.poll;

import jakarta.validation.constraints.NotNull;

public record HospitalInvitationInfoRequest(@NotNull Long passId, @NotNull Long hospitalId) {}
