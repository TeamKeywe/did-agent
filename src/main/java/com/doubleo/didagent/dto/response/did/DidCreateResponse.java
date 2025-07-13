package com.doubleo.didagent.dto.response.did;

import jakarta.validation.constraints.NotBlank;

public record DidCreateResponse(
        @NotBlank String peerDid2,
        @NotBlank String signingKeyMb58,
        @NotBlank String signingPrivBase58,
        @NotBlank String agreementKeyMb58,
        @NotBlank String x25519PrivateMb58) {}
