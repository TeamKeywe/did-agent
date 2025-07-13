package com.doubleo.didagent.dto.request.did;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DidCreateRequest(
        @NotNull List<String> routingKeys, @NotBlank String serviceEndpoint) {}
